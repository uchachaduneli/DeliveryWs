package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.entities.ParcelStatusReason;
import ge.bestline.delivery.ws.entities.Tranzit;
import ge.bestline.delivery.ws.repositories.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping(path = "/tranzit")
public class TranzitController {

    private final TranzitRepository repo;
    private final CarRepository carRepository;
    private final CityRepository cityRepository;
    private final UserRepository userRepository;
    private final WarehouseRepository warehouseRepository;
    private final ParcelStatusReasonRepository statusRepo;

    public TranzitController(TranzitRepository repo,
                             CarRepository carRepository,
                             CityRepository cityRepository,
                             UserRepository userRepository,
                             WarehouseRepository warehouseRepository,
                             ParcelStatusReasonRepository statusRepo) {
        this.repo = repo;
        this.carRepository = carRepository;
        this.cityRepository = cityRepository;
        this.userRepository = userRepository;
        this.warehouseRepository = warehouseRepository;
        this.statusRepo = statusRepo;
    }

    @PostMapping
    @Transactional
    public Tranzit addNew(@RequestBody Tranzit obj) {
        log.info("Adding New Tranzit: " + obj.toString());
        return repo.save(obj);
    }

    @PutMapping(path = "/{id}")
    @Transactional
    public ResponseEntity<Tranzit> updateById(@PathVariable Integer id, @RequestBody Tranzit request) {
        log.info("Updating Tranzit");
        Tranzit existing = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Tranzit Using This ID : " + id));
        log.info("Old Values: " + existing.toString() + "    New Values: " + request.toString());
        existing.setNumber(request.getNumber());
        existing.setTranzitDate(request.getTranzitDate());

        existing.setCar(carRepository.findById(request.getCar().getId()).orElseThrow(() ->
                new ResourceNotFoundException("Can't find Car Using This ID : " + request.getCar().getId())));

        existing.setDriver(userRepository.findById(request.getDriver().getId()).orElseThrow(() ->
                new ResourceNotFoundException("Can't find Driver Using This ID : " + request.getDriver().getId())));

        existing.setDestWarehouse(warehouseRepository.findById(request.getDestWarehouse().getId()).orElseThrow(() ->
                new ResourceNotFoundException("Can't find DestWarehouse Using This ID : " + request.getDestWarehouse().getId())));

        existing.setSenderWarehouse(warehouseRepository.findById(request.getSenderWarehouse().getId()).orElseThrow(() ->
                new ResourceNotFoundException("Can't find SenderWarehouse Using This ID : " + request.getSenderWarehouse().getId())));

        existing.setRouteFrom(cityRepository.findById(request.getRouteFrom().getId()).orElseThrow(() ->
                new ResourceNotFoundException("Can't find RouteFrom City Using This ID : " + request.getRouteFrom().getId())));

        existing.setRouteTo(cityRepository.findById(request.getRouteTo().getId()).orElseThrow(() ->
                new ResourceNotFoundException("Can't find RouteTo City Using This ID : " + request.getRouteTo().getId())));

        Tranzit updatedObj = repo.save(existing);
        return ResponseEntity.ok(updatedObj);
    }

    @PutMapping(path = "/status")
    @Transactional
    public ResponseEntity<Tranzit> updateStatusWithChildBagsAndParcels(@RequestBody Tranzit request) {
        log.info("Updating Tranzit");
        Tranzit existing = repo.findById(request.getId()).orElseThrow(() ->
                new ResourceNotFoundException("Can't find Tranzit Using This ID : " + request.getId()));
        log.info("Old Values: " + existing.toString() + "    New Values: " + request.toString());
        ParcelStatusReason status = statusRepo.findById(request.getStatus().getId()).orElseThrow(() ->
                new ResourceNotFoundException("Can't Find Status to Set Tranzit ID= " + request.getStatus().getId()));
        if (existing.getStatus() == null) {
            existing.setStatus(status);
        } else if (existing.getStatus().getId() != status.getId()) {
            existing.setStatus(status);
//            update inner bags & parcels statuses
        }
        Tranzit updatedObj = repo.save(existing);
        return ResponseEntity.ok(updatedObj);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Map<String, Boolean>> delete(@PathVariable Integer id) {
        Tranzit existing = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : " + id));
        log.info("Deleting Tranzit: " + existing.toString());
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
            Tranzit searchParams) {
        Map<String, Object> resp = new HashMap<>();
        Pageable paging = PageRequest.of(page, rowCount, Sort.by("id").descending());
        Page<Tranzit> pageAuths = null;
        pageAuths = repo.findAll(paging);
        resp.put("items", pageAuths.getContent());
        resp.put("total_count", pageAuths.getTotalElements());
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public Tranzit getById(@PathVariable Integer id) {
        log.info("Getting Tranzit With ID: " + id);
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID"));
    }

}
