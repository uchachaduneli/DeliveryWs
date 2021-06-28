package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.entities.Tranzit;
import ge.bestline.delivery.ws.repositories.*;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/tranzit")
@CrossOrigin(origins = "http://localhost:4200")
public class TranzitController {

    private final TranzitRepository repo;
    private final CarRepository carRepository;
    private final CityRepository cityRepository;
    private final UserRepository userRepository;
    private final WarehouseRepository warehouseRepository;

    public TranzitController(TranzitRepository repo, CarRepository carRepository, CityRepository cityRepository,
                             UserRepository userRepository, WarehouseRepository warehouseRepository) {
        this.repo = repo;
        this.carRepository = carRepository;
        this.cityRepository = cityRepository;
        this.userRepository = userRepository;
        this.warehouseRepository = warehouseRepository;
    }

    @PostMapping
    @Transactional
    public Tranzit addNew(@RequestBody Tranzit obj) {
        return repo.save(obj);
    }

    @PostMapping(path = "/{id}")
    @Transactional
    public ResponseEntity<Tranzit> updateById(@PathVariable Integer id, @RequestBody Tranzit request) {
        Tranzit existing = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Tranzit Using This ID : " + id));
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

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Map<String, Boolean>> delete(@PathVariable Integer id) {
        Tranzit existing = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : " + id));
        existing.setDeleted(1);
        repo.save(existing);
        Map<String, Boolean> resp = new HashMap<>();
        resp.put("deleted", Boolean.TRUE);
        return ResponseEntity.ok(resp);
    }

    @GetMapping
    public Iterable<Tranzit> getAll() {
        return repo.findAll();
    }

    @GetMapping(path = "/{id}")
    public Tranzit getById(@PathVariable Integer id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID"));
    }

}