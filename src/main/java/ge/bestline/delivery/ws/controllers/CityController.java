package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.entities.Car;
import ge.bestline.delivery.ws.entities.City;
import ge.bestline.delivery.ws.repositories.CityRepository;
import ge.bestline.delivery.ws.repositories.ZoneRepository;
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
@RequestMapping(path = "/city")
@CrossOrigin(origins = "http://localhost:4200")
public class CityController {

    private final CityRepository cityRepository;

    private final ZoneRepository zoneRepository;

    public CityController(CityRepository cityRepository, ZoneRepository zoneRepository) {
        this.cityRepository = cityRepository;
        this.zoneRepository = zoneRepository;
    }

    @PostMapping
    @Transactional
    public City addNew(@RequestBody City obj) {
        log.info("Adding New City: " + obj.toString());
        return cityRepository.save(obj);
    }

    @PostMapping(path = "/{id}")
    @Transactional
    public ResponseEntity<City> updateById(@PathVariable Integer id, @RequestBody City request) {
        log.info("Updating City");
        City existing = cityRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : " + id));
        log.info("Old Values: " + existing.toString() + "    New Values: " + request.toString());
        existing.setName(request.getName());
        existing.setCode(request.getCode());
        existing.setZone(zoneRepository.findById(request.getZone().getId()).orElseThrow(() ->
                new ResourceNotFoundException("Can't find Zone Using This ID : " + request.getZone().getId())));
        City updatedObj = cityRepository.save(existing);
        return ResponseEntity.ok(updatedObj);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Map<String, Boolean>> delete(@PathVariable Integer id) {
        City existing = cityRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : " + id));
        log.info("Deleting City: " + existing.toString());
        existing.setDeleted(1);
        cityRepository.save(existing);
        Map<String, Boolean> resp = new HashMap<>();
        resp.put("deleted", Boolean.TRUE);
        return ResponseEntity.ok(resp);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int rowCount,
            City searchParams) {
        Map<String, Object> resp = new HashMap<>();
        Pageable paging = PageRequest.of(page, rowCount, Sort.by("id").descending());
        Page<City> pageAuths = null;
        pageAuths = cityRepository.findAll(paging);
        resp.put("items", pageAuths.getContent());
        resp.put("total_count", pageAuths.getTotalElements());
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public City getCitiesById(@PathVariable Integer id) {
        return cityRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID"));
    }

}
