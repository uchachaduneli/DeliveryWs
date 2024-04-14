package ge.bestline.delivery.ws.controllers;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.dao.DeliveryDetailDao;
import ge.bestline.delivery.ws.dto.DeliveryDetailDTO;
import ge.bestline.delivery.ws.dto.ParcelDTO;
import ge.bestline.delivery.ws.dto.StatusReasons;
import ge.bestline.delivery.ws.dto.TokenUser;
import ge.bestline.delivery.ws.entities.*;
import ge.bestline.delivery.ws.repositories.*;
import ge.bestline.delivery.ws.security.jwt.JwtTokenProvider;
import ge.bestline.delivery.ws.services.BarCodeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@RestController
@RequestMapping(path = "/deliveryDetails")
@AllArgsConstructor
public class DeliveryDetailsController {
    private final DeliveryDetailRepository repo;
    private final BarCodeService barCodeService;
    private final DeliveryDetailDao dao;
    private final RouteRepository routeRepository;
    private final WarehouseRepository warehouseRepository;
    private final UserRepository userRepository;
    private final DeliveryDetailRepository deliveryDetailsRepository;
    private final ParcelRepository parcelRepo;
    private final JwtTokenProvider jwtTokenProvider;
    private final ParcelStatusHistoryRepo statusHistoryRepo;
    private final ParcelStatusReasonRepository statusReasonRepo;

    @PostMapping
    @Transactional
    public DeliveryDetail addNew(@RequestBody DeliveryDetail obj,
                                 HttpServletRequest req) {
        log.info("Adding New DeliveryDetail: " + obj.toString());
        TokenUser requester = jwtTokenProvider.getRequesterUserData(req);
        routeRepository.findById(obj.getRoute().getId()).orElseThrow(
                () -> new ResourceNotFoundException("Can't find Route Using This ID : " + obj.getRoute().getId()));
        User user = userRepository.findById(obj.getUser().getId()).orElseThrow(
                () -> new ResourceNotFoundException("Can't find User Using This ID : " + obj.getUser().getId()));
        warehouseRepository.findById(obj.getWarehouse().getId()).orElseThrow(
                () -> new ResourceNotFoundException("Can't find Warehouse Using This ID : " + obj.getWarehouse().getId()));
        ParcelStatusReason status = null;
//        if (user.hasRole(UserRoles.COURIER.getValue())) {
        if (obj.getCourierOrReception() == 1) {// courier
            if (StringUtils.isBlank(obj.getCarNumber())) {
                throw new RuntimeException("Car Number is mandatory");
            }
            status = statusReasonRepo.findById(StatusReasons.WC.getStatus().getId()).orElseThrow(() ->
                    new ResourceNotFoundException("Can't find WC StatusReason"));
        } else {//reception
//            if (user.hasRole(UserRoles.OFFICE.getValue())) {
            status = statusReasonRepo.findById(StatusReasons.CC.getStatus().getId()).orElseThrow(() ->
                    new ResourceNotFoundException("Can't find CC StatusReason"));
//            }
        }
        if (status != null) {
            List<Parcel> loadedParcels = parcelRepo.findByIdInAndDeleted(obj.getParcels().stream().map(Parcel::getId).collect(Collectors.toList()), 2);
            List<ParcelStatusHistory> statusHistories = new ArrayList<>();
            for (Parcel p : loadedParcels) {
                p.setStatus(status);
                statusHistories.add(new ParcelStatusHistory(
                        p,
                        status.getStatus().getName(),
                        status.getStatus().getCode(),
                        status.getName(),
                        new Timestamp(new Date().getTime()),
                        new User(requester.getId())));
            }
            parcelRepo.saveAll(loadedParcels);
            statusHistoryRepo.saveAll(statusHistories);
        } else {
            throw new ResourceNotFoundException("Selected User Has No COURIER OR OFFICE ROLE");
        }

        return repo.save(obj);
    }

    @SneakyThrows
    @GetMapping
    public MappingJacksonValue getAll(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int rowCount,
            DeliveryDetailDTO srchParams) {
        log.info("Getting DeliveryDetails with params: " + srchParams);
        if (StringUtils.isNotBlank(srchParams.getStrCreatedTime())) {
            srchParams.setCreatedTime(ParcelDTO.convertStrDateToDateObj(srchParams.getStrCreatedTime()));
        }
        if (StringUtils.isNotBlank(srchParams.getStrCreatedTimeTo())) {
            srchParams.setCreatedTimeTo(ParcelDTO.convertStrDateToDateObj(srchParams.getStrCreatedTimeTo()));
        }

        Set<String> fieldsToExclude = Parcel.fieldsNameList();
        fieldsToExclude.removeAll(Arrays.asList("id", "deliveryTime", "barCode", "weight", "senderName", "senderIdentNumber",
                "receiverName", "receiverIdentNumber", "status", "count", "receiverAddress", "receiverPhone"));
        SimpleBeanPropertyFilter simpleBeanPropertyFilter = SimpleBeanPropertyFilter.serializeAllExcept(fieldsToExclude);
        FilterProvider filterProvider = new SimpleFilterProvider().addFilter("fieldsFilter", simpleBeanPropertyFilter);
        Map<String, Object> response = dao.findAll(page, rowCount, srchParams);
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(response);
        mappingJacksonValue.setFilters(filterProvider);
        return mappingJacksonValue;
    }

    @GetMapping("barcode")
    public ResponseEntity<String> getBarCodeForNewDetails() {
        log.info("Getting Barcode For new Delivery Details Started");
        String barcode = barCodeService.getBarcodes(1).get(0);
        if (deliveryDetailsRepository.findByDetailBarCode(barcode).isPresent()) {
            // try to generate one more time to find not existing one in delivery details table
            barcode = barCodeService.getBarcodes(1).get(0);
        }
        return new ResponseEntity<>(barcode, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public DeliveryDetail getById(@PathVariable Integer id) {
        log.info("Getting DeliveryDetail With ID: " + id);
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID"));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Map<String, Boolean>> delete(@PathVariable Integer id) {
        DeliveryDetail existing = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : " + id));
        log.info("Deleting DeliveryDetail: " + existing.toString());
        repo.delete(existing);
        Map<String, Boolean> resp = new HashMap<>();
        resp.put("deleted", Boolean.TRUE);
        return ResponseEntity.ok(resp);
    }
}
