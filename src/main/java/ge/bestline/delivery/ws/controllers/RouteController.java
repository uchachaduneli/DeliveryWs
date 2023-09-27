package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.entities.Route;
import ge.bestline.delivery.ws.repositories.CityRepository;
import ge.bestline.delivery.ws.repositories.RouteRepository;
import lombok.extern.log4j.Log4j2;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping(path = "/route")
public class RouteController {

    private final RouteRepository repo;
    private final CityRepository cityRepository;

    public RouteController(RouteRepository repo, CityRepository cityRepository) {
        this.repo = repo;
        this.cityRepository = cityRepository;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        return new ResponseEntity<>("მითითებული ქალაქში მსგავსი მარშრუტი უკვე არსებობს", HttpStatus.BAD_REQUEST);
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
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int rowCount,
            Route searchParams) {
        Map<String, Object> resp = new HashMap<>();
        Pageable paging = PageRequest.of(page, rowCount, Sort.by("id").descending());
        Page<Route> pageAuths = null;
        if (searchParams.getCity() != null) {
            pageAuths = repo.findByCityIdAndDeleted(searchParams.getCity().getId(), paging, 2);
        } else if (searchParams.getName() != null) {
            pageAuths = repo.findByNameContainingIgnoreCaseAndDeleted(searchParams.getName(), paging, 2);
        } else {
            pageAuths = repo.findAll(paging);
        }
        resp.put("items", pageAuths.getContent());
        resp.put("total_count", pageAuths.getTotalElements());
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public Route getRoutesById(@PathVariable Integer id) {
        log.info("Getting Route With ID: " + id);
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID"));
    }

    @GetMapping(path = "/byCityId/{id}")
    public Iterable<Route> getRoutesByCityId(@PathVariable Integer id) {
        log.info("Getting Route With City ID: " + id);
        return repo.findByCityIdAndDeleted(id, 2);
    }

}
