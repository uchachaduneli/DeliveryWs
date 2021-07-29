package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.entities.Route;
import ge.bestline.delivery.ws.entities.Services;
import ge.bestline.delivery.ws.entities.Warehouse;
import ge.bestline.delivery.ws.repositories.CityRepository;
import ge.bestline.delivery.ws.repositories.WarehouseRepository;
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
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int rowCount,
            Warehouse searchParams) {
        Map<String, Object> resp = new HashMap<>();
        Pageable paging = PageRequest.of(page, rowCount, Sort.by("id").descending());
        Page<Warehouse> pageAuths = null;
        pageAuths = repo.findAll(paging);
        resp.put("items", pageAuths.getContent());
        resp.put("total_count", pageAuths.getTotalElements());
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public Warehouse getById(@PathVariable Integer id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Warehouse Using This ID"));
    }

    @GetMapping(path = "/byCityId/{id}")
    public Iterable<Warehouse> getRoutesByCityId(@PathVariable Integer id) {
        log.info("Getting Route With City ID: " + id);
        return repo.findByCity_Id(id);
    }

}
