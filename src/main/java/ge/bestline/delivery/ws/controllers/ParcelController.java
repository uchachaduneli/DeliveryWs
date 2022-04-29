package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.dao.ParcelDao;
import ge.bestline.delivery.ws.dto.DeliveryDetailParcelDTO;
import ge.bestline.delivery.ws.dto.ParcelWithPackagesDTO;
import ge.bestline.delivery.ws.entities.*;
import ge.bestline.delivery.ws.repositories.*;
import ge.bestline.delivery.ws.services.BarCodeService;
import lombok.extern.log4j.Log4j2;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping(path = "/parcel")
public class ParcelController {

    private final ParcelRepository repo;
    private final VolumeWeightIndexRepository volumeWeightIndexRepository;
    private final PackagesRepository packagesRepo;
    private final ParcelDao dao;
    private final ParselStatusHistoryRepo statusHistoryRepo;
    private final ParcelStatusReasonRepository statusReasonRepo;
    private final UserRepository userRepository;
    private final RouteRepository routeRepository;
    private final BarCodeService barCodeService;

    public ParcelController(ParcelRepository repo,
                            VolumeWeightIndexRepository volumeWeightIndexRepository,
                            PackagesRepository packagesRepo, ParcelDao dao,
                            ParselStatusHistoryRepo statusHistoryRepo,
                            ParcelStatusReasonRepository statusReasonRepo,
                            UserRepository userRepository,
                            RouteRepository routeRepository,
                            BarCodeService barCodeService) {
        this.repo = repo;
        this.volumeWeightIndexRepository = volumeWeightIndexRepository;
        this.packagesRepo = packagesRepo;
        this.dao = dao;
        this.statusHistoryRepo = statusHistoryRepo;
        this.statusReasonRepo = statusReasonRepo;
        this.userRepository = userRepository;
        this.routeRepository = routeRepository;
        this.barCodeService = barCodeService;
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        return new ResponseEntity<>("მსგავსი ამანათი უკვე არსებობს", HttpStatus.BAD_REQUEST);
    }

    @PostMapping(path = "/prePrint/{count}")
    @Transactional
    public ResponseEntity<List<Parcel>> preGeneration(@PathVariable Integer count) {
        List<Parcel> res = new ArrayList<>();
        for(String barcode : barCodeService.getBarcodes(count)){
            res.add(repo.save(new Parcel(barcode)));
        }
        return ResponseEntity.ok(res);
    }

    @PostMapping
    @Transactional
    public Parcel addNew(@RequestBody Parcel obj) {
        log.info("Adding New Parcel: " + obj.toString());
        User courier = userRepository.findByRouteId(obj.getRoute().getId()).orElseThrow(
                () -> new ResourceNotFoundException("Can't find Courier Using This Route ID : " + obj.getRoute().getId()));
        Route route = routeRepository.findById(obj.getRoute().getId()).orElseThrow(
                () -> new ResourceNotFoundException("Can't find Route Using This ID : " + obj.getRoute().getId()));
        obj.setCourier(courier);
        obj.setRoute(route);
        obj.setBarCode(barCodeService.getBarcodes(1).get(0));
        Parcel parcel = repo.save(obj);
        ParcelStatusReason psr = statusReasonRepo.findById(1).orElseThrow(() ->
                new ResourceNotFoundException("Can't find Default StatusReason Record At ID=1 For Parcels Status History"));
        statusHistoryRepo.save(new ParcelStatusHistory(
                parcel,
                psr.getStatus().getName(),
                psr.getStatus().getCode(),
                psr.getName()));
        return parcel;
    }

    @GetMapping(path = "/statusHistory/{id}")
    public ResponseEntity<List<ParcelStatusHistory>> getParcelStatusHistoryByParcelId(@PathVariable Integer id) {
        log.info("Getting ParcelStatusHistory By Parcel ID: " + id);
        return ResponseEntity.ok(statusHistoryRepo.findByParcelId(id));
    }

    @GetMapping(path = "/byBarCode/{barCode}")
    public ResponseEntity<Parcel> getParcelByBarCode(@PathVariable String barCode) {
        log.info("Getting Parcel By BarCode : " + barCode);
        Parcel parcel = repo.findByBarCode(barCode).orElseThrow(() -> new ResourceNotFoundException("Can't find Parcel Using This BarCode : " + barCode));
        return ResponseEntity.ok(parcel);
    }

    @PutMapping(path = "/{id}")
    @Transactional
    public ResponseEntity<Parcel> updateById(@PathVariable Integer id, @RequestBody Parcel request) {
        log.info("Updating Parcel");
        Parcel existing = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : " + id));
        User courier = userRepository.findByRouteId(request.getRoute().getId()).orElseThrow(() -> new ResourceNotFoundException("Can't find Courier Using This Route ID : " + request.getRoute().getId()));
        Route route = routeRepository.findById(request.getRoute().getId()).orElseThrow(() -> new ResourceNotFoundException("Can't find Route Using This ID : " + request.getRoute().getId()));
        log.info("Old Values: " + existing.toString() + "    New Values: " + request.toString());
        if (request.getStatus().getId() != existing.getStatus().getId()) {
            statusHistoryRepo.save(new ParcelStatusHistory(existing
                    , existing.getStatus().getStatus().getName()
                    , existing.getStatus().getStatus().getCode()
                    , existing.getStatus().getName()
            ));
            existing.setStatus(request.getStatus());
        }
        existing.setSendSmsToSender(request.getSendSmsToSender());
        existing.setSendSmsToReceiver(request.getSendSmsToReceiver());
        existing.setReceiverPhone(request.getReceiverPhone());
        existing.setSenderPhone(request.getSenderPhone());
        existing.setRoute(route);
        existing.setCourier(courier);
        Parcel updatedObj = repo.save(existing);
        return ResponseEntity.ok(updatedObj);
    }

    @PutMapping(path = "/deliveryDetailParcel")
    @Transactional
    public ResponseEntity<Parcel> updateFromDeliveryDetail(@RequestBody DeliveryDetailParcelDTO request) {
        log.info("Updating Parcel");
        Parcel existing = repo.findById(request.getId()).orElseThrow(() ->
                new ResourceNotFoundException("Can't find Record Using This ID : " + request.getId()));
        log.info("Old Values: " + existing.toString() + "    New Values: " + request.toString());
        if (request.getStatus().getId() != existing.getStatus().getId()) {
            statusHistoryRepo.save(new ParcelStatusHistory(existing
                    , existing.getStatus().getStatus().getName()
                    , existing.getStatus().getStatus().getCode()
                    , existing.getStatus().getName()
            ));
            existing.setStatus(request.getStatus());
        }
        existing.setReceiverName(request.getReceiverName());
        existing.setReceiverIdentNumber(request.getReceiverIdentNumber());
        existing.setDeliveryTime(request.getDeliveryTime());
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
            Parcel searchParams) {
        return new ResponseEntity<>(dao.findAll(page, rowCount, searchParams), HttpStatus.OK);
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
        for (Parcel p : repo.findByIdIn(ides)) {
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
