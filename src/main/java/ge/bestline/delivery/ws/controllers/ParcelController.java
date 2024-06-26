package ge.bestline.delivery.ws.controllers;

import com.google.firebase.messaging.FirebaseMessagingException;
import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.dao.ParcelDao;
import ge.bestline.delivery.ws.dto.*;
import ge.bestline.delivery.ws.entities.*;
import ge.bestline.delivery.ws.repositories.*;
import ge.bestline.delivery.ws.security.jwt.JwtTokenProvider;
import ge.bestline.delivery.ws.services.BarCodeService;
import ge.bestline.delivery.ws.services.FirebaseMessagingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;

@Data
@Log4j2
@RestController
@RequestMapping(path = "/parcel")
public class ParcelController {

    private final ParcelRepository repo;
    private final VolumeWeightIndexRepository volumeWeightIndexRepository;
    private final PackagesRepository packagesRepo;
    private final ParcelDao dao;
    private final ParcelStatusHistoryRepo statusHistoryRepo;
    private final ParcelStatusReasonRepository statusReasonRepo;
    private final UserRepository userRepository;
    private final RouteRepository routeRepository;
    private final BarCodeService barCodeService;
    private final JwtTokenProvider jwtTokenProvider;
    private final FirebaseMessagingService firebaseService;
    private final UserRepository userRepo;

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        return new ResponseEntity<>("მსგავსი ამანათი უკვე არსებობს", HttpStatus.BAD_REQUEST);
    }


    @PostMapping("/send-notification")
    public String sendNotification(@RequestBody FirebaseNote note,
                                   @RequestParam String token) throws FirebaseMessagingException {
        return firebaseService.sendNotification(note, token);
    }

    @PostMapping(path = "/prePrint/{count}")
    @Transactional
    public ResponseEntity<List<Parcel>> preGeneration(@PathVariable Integer count) {
        List<Parcel> res = new ArrayList<>();
        for (String barcode : barCodeService.getBarcodes(count)) {
            res.add(repo.save(new Parcel(barcode, true)));
        }
        return ResponseEntity.ok(res);
    }

    @PostMapping
    @Transactional
    public Parcel addNew(@RequestBody Parcel obj,
                         HttpServletRequest req) {
        log.info("Adding New Parcel: " + obj.toString());
        TokenUser requester = jwtTokenProvider.getRequesterUserData(req);
        ParcelStatusReason psr = null;
        // if parcel is added from admin portal
        if (!requester.isFromGlobalSite()) {
            if (obj.getRoute() != null && obj.getRoute().getId() > 0) {
                User courier = userRepository.findByRouteIdAndDeleted(obj.getRoute().getId(), 2).orElseThrow(
                        () -> new ResourceNotFoundException("Can't find Courier Using This Route ID : " + obj.getRoute().getId()));
                Route route = routeRepository.findByIdAndDeleted(obj.getRoute().getId(), 2).orElseThrow(
                        () -> new ResourceNotFoundException("Can't find Route Using This ID : " + obj.getRoute().getId()));
                obj.setCourier(courier);
                try {
                    if (StringUtils.isNotBlank(courier.getFirebaseToken())) {
                        firebaseService.sendNotification(new FirebaseNote("ახალი ამანათი", "გთხოვთ იხილოთ ახალი გამოძახებების განყოფილება"), courier.getFirebaseToken());
                    } else {
                        log.error("Can't Send Notiff To Courier Via Firebase, Courier Token is Null or blank. -> " + courier.getFirebaseToken());
                    }
                } catch (Exception e) {
                    log.error("Can't Send Notiff To Courier Via Firebase " + courier.getName() + " " + courier.getLastName() + " " + courier.getPersonalNumber(), e);
//                    throw new RuntimeException("Can't Send Notiff To Courier Via Firebase " + courier.getName() + " " + courier.getLastName() + " " + courier.getPersonalNumber(), e);
                }
                obj.setRoute(route);
                psr = statusReasonRepo.findById(StatusReasons.RG.getStatus().getId()).orElseThrow(() ->
                        new ResourceNotFoundException("Can't find parcel status reason with RG - enum's value"));
            }
            psr = statusReasonRepo.findById(StatusReasons.PP.getStatus().getId()).orElseThrow(() ->
                    new ResourceNotFoundException("Can't find StatusReason Record with PP - enum's value"));
        }
        // if parcel added from global
        if (requester.isFromGlobalSite()) {
            obj.setAddedFromGlobal(true);
            psr = statusReasonRepo.findById(StatusReasons.PP.getStatus().getId()).orElseThrow(() ->
                    new ResourceNotFoundException("Can't find Default StatusReason Record At ID=1 For Parcels Status History"));
        } else {
            obj.setAddedFromGlobal(false);
        }
        obj.setStatus(psr);
        obj.setAuthor(new User(requester.getId()));
        obj.setBarCode(barCodeService.getBarcodes(1).get(0));
        Parcel parcel = repo.save(obj);
        statusHistoryRepo.save(new ParcelStatusHistory(
                parcel,
                psr.getStatus().getName(),
                psr.getStatus().getCode(),
                psr.getName(),
                new Timestamp(new Date().getTime()),
                new User(requester.getId()))
        );
        return parcel;
    }

    @GetMapping(path = "/statusHistory/{id}")
    public ResponseEntity<List<ParcelStatusHistory>> getParcelStatusHistoryByParcelId(@PathVariable Integer id) {
        log.info("Getting ParcelStatusHistory By Parcel ID: " + id);
        return ResponseEntity.ok(statusHistoryRepo.findByParcelIdOrderByStatusDateTimeAsc(id));
    }

    @GetMapping(path = "/byBarCode/{barCode}")
    public ResponseEntity<Parcel> getParcelByBarCode(@PathVariable String barCode) {
        log.info("Getting Parcel By BarCode : " + barCode);
        Parcel parcel = repo.findByBarCodeAndDeleted(barCode, 2).orElseThrow(() -> new ResourceNotFoundException("Can't find Parcel Using This BarCode : " + barCode));
        return ResponseEntity.ok(parcel);
    }

    @PutMapping(path = "/{id}")
    @Transactional
    public ResponseEntity<Parcel> updateById(@PathVariable Integer id,
                                             @RequestBody Parcel request,
                                             HttpServletRequest req) {
        log.info("Updating Parcel");
        TokenUser requester = jwtTokenProvider.getRequesterUserData(req);
        Parcel existing = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : " + id));
        log.info("Old Values: " + existing.toString() + "    New Values: " + request.toString());
        if (request.getStatus().getId() != existing.getStatus().getId()) {
            ParcelStatusReason status = statusReasonRepo.findById(request.getStatus().getId()).orElseThrow(() ->
                    new ResourceNotFoundException("Can't find parcel status reason with ID " + request.getStatus().getId()));
            statusHistoryRepo.save(new ParcelStatusHistory(existing
                    , status.getStatus().getName()
                    , status.getStatus().getCode()
                    , status.getName()
                    , new Timestamp(new Date().getTime())
                    , new User(requester.getId())
            ));
            existing.setStatus(request.getStatus());
        }

        if (request.getRoute() != null && request.getRoute().getId() > 0 && !requester.isFromGlobalSite()) {
            User courier = userRepository.findByRouteIdAndDeleted(request.getRoute().getId(), 2).orElseThrow(
                    () -> new ResourceNotFoundException("Can't find Courier Using This Route ID : " + request.getRoute().getId()));
            Route route = routeRepository.findByIdAndDeleted(request.getRoute().getId(), 2).orElseThrow(
                    () -> new ResourceNotFoundException("Can't find Route Using This ID : " + request.getRoute().getId()));
            existing.setCourier(courier);
            if (existing.getStatus().getId() == StatusReasons.PP.getStatus().getId()) {
                existing.setStatus(StatusReasons.RG.getStatus());
                try {
                    if (StringUtils.isNotBlank(courier.getFirebaseToken())) {
                        firebaseService.sendNotification(new FirebaseNote("ახალი ამანათი", "გთხოვთ იხილოთ ახალი გამოძახებების განყოფილება"), courier.getFirebaseToken());
                    } else {
                        log.error("Can't Send Notiff To Courier Via Firebase, Courier Token is Null or blank. -> " + courier.getFirebaseToken());
                    }
                } catch (Exception e) {
                    log.error("Can't Send Notiff To Courier Via Firebase " + courier.getName() + " " + courier.getLastName() + " " + courier.getPersonalNumber(), e);
//                    throw new RuntimeException("Can't Send Notiff To Courier Via Firebase " + courier.getName() + " " + courier.getLastName() + " " + courier.getPersonalNumber(), e);
                }
            }
            existing.setRoute(route);
        }

        if (request.getTotalPrice() != null && !requester.isFromGlobalSite() && existing.getTotalPrice() != request.getTotalPrice()) {
            existing.setTotalPrice(request.getTotalPrice());
        }

        existing.setSenderIdentNumber(request.getSenderIdentNumber());
        existing.setSenderCity(request.getSenderCity());
        existing.setSenderName(request.getSenderName());
        existing.setSenderAddress(request.getSenderAddress());
        existing.setSenderPhone(request.getSenderPhone());
        existing.setSendSmsToSender(request.getSendSmsToSender());
        existing.setSenderContactPerson(request.getSenderContactPerson());

        existing.setReceiverCity(request.getReceiverCity());
        existing.setReceiverName(request.getReceiverName());
        existing.setReceiverAddress(request.getReceiverAddress());
        existing.setReceiverIdentNumber(request.getReceiverIdentNumber());
        existing.setSendSmsToReceiver(request.getSendSmsToReceiver());
        existing.setReceiverPhone(request.getReceiverPhone());
        existing.setReceiverContactPerson(request.getReceiverContactPerson());

        existing.setPayerCity(request.getPayerCity());
        existing.setPayerName(request.getPayerName());
        existing.setPayerAddress(request.getPayerAddress());
        existing.setPayerIdentNumber(request.getPayerIdentNumber());
        existing.setPayerPhone(request.getPayerPhone());
        existing.setPayerContactPerson(request.getPayerContactPerson());

        existing.setPayerSide(request.getPayerSide());
        existing.setCount(request.getCount());
        existing.setWeight(request.getWeight());
        existing.setDeliveryType(request.getDeliveryType());
        existing.setPaymentType(request.getPaymentType());
        existing.setPackageType(request.getPackageType());
        existing.setContent(request.getContent());
        existing.setComment(request.getComment());

        Parcel updatedObj = repo.save(existing);
        return ResponseEntity.ok(updatedObj);
    }

    @PutMapping("/multipleStatusUpdate")
    @Transactional
    public ResponseEntity<List<Parcel>> updateMultiplesStatusByBarCode(@RequestBody StatusManagerReqDTO request
            , HttpServletRequest req) throws ParseException {
        log.info("Update Multiple Parcels Status By BarCode");
        request.setStatusDateTime(ParcelDTO.convertStrDateToDateObj(request.getStrStatusDateTime()));
        TokenUser requester = jwtTokenProvider.getRequesterUserData(req);
        ParcelStatusReason status = statusReasonRepo.findById(request.getStatusId()).orElseThrow(() ->
                new ResourceNotFoundException("Can't find Status Using This ID : " + request.getStatusId()));
        List<Parcel> res = new ArrayList<>();
        for (Parcel p : repo.findByBarCodeInAndDeleted(request.getBarCodes(), 2)) {
            if (p.getStatus().getId() != status.getId()) {
                statusHistoryRepo.save(new ParcelStatusHistory(p
                        , status.getName()
                        , status.getStatus().getCode()
                        , request.getNote()
                        , request.getStatusDateTime()
                        , new User(requester.getId())
                ));
            }
            p.setStatus(status);
            p.setStatusNote(request.getNote());
            p.setStatusDateTime(request.getStatusDateTime());
            res.add(repo.save(p));
        }
        return ResponseEntity.ok(res);
    }

    @PutMapping(path = "/deliveryDetailParcel")
    @Transactional
    public ResponseEntity<Parcel> updateFromDeliveryDetail(@RequestBody DeliveryDetailParcelDTO request
            , HttpServletRequest req) {
        log.info(" updateFromDeliveryDetail started");
        TokenUser requester = jwtTokenProvider.getRequesterUserData(req);
        ParcelStatusReason status = statusReasonRepo.findById(request.getStatus().getId()).orElseThrow(() ->
                new ResourceNotFoundException("Can't find Status Using This ID : " + request.getStatus().getId()));
        Parcel existing = repo.findById(request.getId()).orElseThrow(() ->
                new ResourceNotFoundException("Can't find Record Using This ID : " + request.getId()));
        log.info("updateFromDeliveryDetail Old Values: " + existing.toString() + "    New Values: " + request.toString());
        if (request.getStatus().getId() != existing.getStatus().getId()) {
            statusHistoryRepo.save(new ParcelStatusHistory(existing
                    , status.getStatus().getName()
                    , status.getStatus().getCode()
                    , existing.getStatusNote()
                    , new Timestamp(new Date().getTime())
                    , new User(requester.getId())
            ));
            existing.setStatus(status);
        }
        existing.setReceiverName(request.getReceiverName());
        existing.setReceiverIdentNumber(request.getReceiverIdentNumber());
        existing.setDeliveryTime(request.getDeliveryTime());
        existing.setStatusNote(request.getStatusNote());
        Parcel updatedObj = repo.save(existing);
        return ResponseEntity.ok(updatedObj);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Map<String, Boolean>> delete(@PathVariable Integer id) {
        Parcel existing = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : " + id));
        log.info("Deleting Parcel: " + existing.toString());
        existing.setDeleted(1);
        repo.save(existing);
        Map<String, Boolean> resp = new HashMap<>();
        resp.put("deleted", Boolean.TRUE);
        return ResponseEntity.ok(resp);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int rowCount,
            ParcelDTO srchParams,
            HttpServletRequest req) throws ParseException {
        TokenUser requester = jwtTokenProvider.getRequesterUserData(req);
        if (requester.isFromGlobalSite()) {
            srchParams.setSearchingFromGlobal(true);
            // roca globalidan akitxavs daubrunebs tavis damatebulebs an romelshic gamgzavnadaa moxseniebuli
            Optional<User> user = userRepo.findById(requester.getId());
            if (user.isPresent()) {
                srchParams.setAuthorId(requester.getId());
                srchParams.setSenderIdentNumber(user.get().getPersonalNumber());
            }
        }

        if (StringUtils.isNotBlank(srchParams.getStrCreatedTime())) {
            srchParams.setCreatedTime(ParcelDTO.convertStrDateToDateObj(srchParams.getStrCreatedTime()));
        }
        if (StringUtils.isNotBlank(srchParams.getStrCreatedTimeTo())) {
            srchParams.setCreatedTimeTo(ParcelDTO.convertStrDateToDateObj(srchParams.getStrCreatedTimeTo()));
        }
        if (StringUtils.isNotBlank(srchParams.getStrDeliveryTime())) {
            srchParams.setDeliveryTime(ParcelDTO.convertStrDateToDateObj(srchParams.getStrDeliveryTime()));
        }
        if (StringUtils.isNotBlank(srchParams.getStrDeliveryTimeTo())) {
            srchParams.setDeliveryTimeTo(ParcelDTO.convertStrDateToDateObj(srchParams.getStrDeliveryTimeTo()));
        }
        return new ResponseEntity<>(dao.findAll(page, rowCount, srchParams, false, requester.isFromGlobalSite()), HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public Parcel getById(@PathVariable Integer id) {
        log.info("Getting Parcel With ID: " + id);
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID"));
    }

    @GetMapping(path = "/byIdesIn")
    public ResponseEntity<List<ParcelWithPackagesDTO>> getById(@RequestParam List<Integer> ides) {
        log.info("Getting Parcels ID In: " + ides.toString());
        List<ParcelWithPackagesDTO> res = new ArrayList<>();
        for (Parcel p : repo.findByIdInAndDeleted(ides, 2)) {
            res.add(new ParcelWithPackagesDTO(p, packagesRepo.findByParcelId(p.getId())));
        }
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping(path = "/package/{id}")
    public ResponseEntity<List<Packages>> getPackagesByParcelId(@PathVariable Integer id) {
        log.info("Getting Packages By Parcel ID: " + id);
        return ResponseEntity.ok(packagesRepo.findByParcelId(id));
    }

    @PostMapping("/package")
    @Transactional
    public ResponseEntity<List<Packages>> addNewPackage(@RequestBody List<Packages> list) {
        log.info("Adding New Packages: " + list.toString());
        List<Packages> res = new ArrayList<>();
        for (Packages p : list) {
            res.add(packagesRepo.save(p));
        }
        return ResponseEntity.ok(res);
    }

    @PutMapping(path = "/package")
    @Transactional
    public ResponseEntity<List<Packages>> updatePackageById(@RequestBody List<Packages> list) {
        log.info("Updating Packages " + list.toString());
        List<Packages> res = new ArrayList<>();
        for (Packages p : list) {
            Packages existing = packagesRepo.findById(p.getId()).orElseThrow(() -> new ResourceNotFoundException("Can't find Package Using This ID : " + p.getId()));
            existing.setLength(p.getLength());
            existing.setWidth(p.getWidth());
            existing.setHeight(p.getHeight());
            existing.setVolumeWeight(p.getVolumeWeight());
            existing.setPlombNumber(p.getPlombNumber());
            existing.setBoxNumber(p.getBoxNumber());
            res.add(packagesRepo.save(existing));
        }
        return ResponseEntity.ok(res);
    }

    @PostMapping(path = "/volumeWeightIndex")
    @Transactional
    public ResponseEntity<VolumeWeightIndex> updateVolumeWeightIndex(@RequestBody VolumeWeightIndex request) {
        log.info("Updating volumeWeight Index");
        VolumeWeightIndex existing = volumeWeightIndexRepository
                .findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : " + request.getId()));
        log.info("Old Values: " + existing.toString() + "  New Values: " + request.toString());
        existing.setAmount(request.getAmount());
        VolumeWeightIndex updatedObj = volumeWeightIndexRepository.save(existing);
        return ResponseEntity.ok(updatedObj);
    }

    @GetMapping(path = "/volumeWeightIndex")
    public ResponseEntity<VolumeWeightIndex> getPackagesByParcelId() {
        log.info("Getting volumeWeightIndex ");
        List<VolumeWeightIndex> tmp = volumeWeightIndexRepository.findAll();
        return ResponseEntity.ok(tmp.isEmpty() ? null : tmp.get(0));
    }
}
