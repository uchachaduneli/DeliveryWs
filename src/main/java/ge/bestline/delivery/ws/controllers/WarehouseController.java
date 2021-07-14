package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.entities.Warehouse;
import ge.bestline.delivery.ws.repositories.CityRepository;
import ge.bestline.delivery.ws.repositories.WarehouseRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping(path = "/warehouse")
@CrossOrigin(origins = "http://localhost:4200")
public class WarehouseController {

    private final WarehouseRepository repo;
    private final CityRepository cityRepository;

    public WarehouseController(WarehouseRepository repo, CityRepository cityRepository) {
        this.repo = repo;
        this.cityRepository = cityRepository;
    }

    @PostMapping
    @Transactional
    public Warehouse addNew(@RequestBody Warehouse obj) {
        return repo.save(obj);
    }

    @PostMapping(path = "/{id}")
    @Transactional
    public ResponseEntity<Warehouse> updateById(@PathVariable Integer id, @RequestBody Warehouse request) {
        Warehouse existing = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Warehouse Using This ID : " + id));
        existing.setName(request.getName());
        existing.setCity(cityRepository.findById(request.getCity().getId()).orElseThrow(() ->
                new ResourceNotFoundException("Can't find City Using This ID : " + request.getCity().getId())));
        Warehouse updatedObj = repo.save(existing);
        return ResponseEntity.ok(updatedObj);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Map<String, Boolean>> delete(@PathVariable Integer id) {
        Warehouse existing = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Warehouse Using This ID : " + id));
        existing.setDeleted(1);
        repo.save(existing);
        Map<String, Boolean> resp = new HashMap<>();
        resp.put("deleted", Boolean.TRUE);
        return ResponseEntity.ok(resp);
    }

    @GetMapping
    public Iterable<Warehouse> getAll() {
        return repo.findAll();
    }

    @GetMapping(path = "/{id}")
    public Warehouse getById(@PathVariable Integer id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Warehouse Using This ID"));
    }

}
