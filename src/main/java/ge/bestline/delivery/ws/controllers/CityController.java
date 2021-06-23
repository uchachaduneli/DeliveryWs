package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.entities.City;
import ge.bestline.delivery.ws.repositories.CityRepository;
import ge.bestline.delivery.ws.repositories.ZoneRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
        return cityRepository.save(obj);
    }

    @PostMapping(path = "/{id}")
    @Transactional
    public ResponseEntity<City> updateById(@PathVariable Integer id, @RequestBody City request) {
        City existing = cityRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : " + id));
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
        existing.setDeleted(1);
        cityRepository.save(existing);
        Map<String, Boolean> resp = new HashMap<>();
        resp.put("deleted", Boolean.TRUE);
        return ResponseEntity.ok(resp);
    }

    @GetMapping
    public Iterable<City> getAllCities() {
        return cityRepository.findAll();
    }

    @GetMapping(path = "/{id}")
    public City getCitiesById(@PathVariable Integer id) {
        return cityRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID"));
    }

}
