package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.entities.Route;
import ge.bestline.delivery.ws.repositories.CityRepository;
import ge.bestline.delivery.ws.repositories.RouteRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping(path = "/route")
@CrossOrigin(origins = "http://localhost:4200")
public class RouteController {

    private final RouteRepository repo;
    private final CityRepository cityRepository;

    public RouteController(RouteRepository repo, CityRepository cityRepository) {
        this.repo = repo;
        this.cityRepository = cityRepository;
    }

    @PostMapping
    @Transactional
    public Route addNew(@RequestBody Route obj) {
        log.info("Adding New Route: " + obj.toString());
        return repo.save(obj);
    }

    @PostMapping(path = "/{id}")
    @Transactional
    public ResponseEntity<Route> updateById(@PathVariable Integer id, @RequestBody Route request) {
        log.info("Updating Route");
        Route existing = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : " + id));
        log.info("Old Values: " + existing.toString() + "    New Values: " + request.toString());
        existing.setName(request.getName());
        existing.setNote(request.getNote());
        existing.setCity(cityRepository.findById(request.getCity().getId()).orElseThrow(() ->
                new ResourceNotFoundException("Can't find City Using This ID : " + request.getCity().getId())));
        Route updatedObj = repo.save(existing);
        return ResponseEntity.ok(updatedObj);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Map<String, Boolean>> delete(@PathVariable Integer id) {
        Route existing = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : " + id));
        log.info("Deleting Route: " + existing.toString());
        existing.setDeleted(1);
        repo.save(existing);
        Map<String, Boolean> resp = new HashMap<>();
        resp.put("deleted", Boolean.TRUE);
        return ResponseEntity.ok(resp);
    }

    @GetMapping
    public Iterable<Route> getAllRoutes() {
        return repo.findAll();
    }

    @GetMapping(path = "/{id}")
    public Route getRoutesById(@PathVariable Integer id) {
        log.info("Getting Route With ID: " + id);
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID"));
    }

    @GetMapping(path = "/byCityId/{id}")
    public Iterable<Route> getRoutesByCityId(@PathVariable Integer id) {
        log.info("Getting Route With City ID: " + id);
        return repo.findByCity_Id(id);
    }

}
