package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.entities.Zone;
import ge.bestline.delivery.ws.repositories.ZoneRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/zone")
@CrossOrigin(origins = "http://localhost:4200")
public class ZoneController {

    private final ZoneRepository repo;

    public ZoneController(ZoneRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    @Transactional
    public Zone addNew(@RequestBody Zone obj) {
        return repo.save(obj);
    }

    @PostMapping(path = "/{id}")
    @Transactional
    public ResponseEntity<Zone> updateById(@PathVariable Integer id, @RequestBody Zone request) {
        Zone existing = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : " + id));
        existing.setName(request.getName());
        existing.setWeight(request.getWeight());
        existing.setWeightLabel(request.getWeightLabel());
        Zone updatedObj = repo.save(existing);
        return ResponseEntity.ok(updatedObj);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Map<String, Boolean>> delete(@PathVariable Integer id) {
        Zone existing = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : " + id));
        existing.setDeleted(1);
        repo.save(existing);
        Map<String, Boolean> resp = new HashMap<>();
        resp.put("deleted", Boolean.TRUE);
        return ResponseEntity.ok(resp);
    }

    @GetMapping
    public Iterable<Zone> getAllZones() {
        return repo.findAll();
    }

    @GetMapping(path = "/{id}")
    public Zone getZonesById(@PathVariable Integer id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID"));
    }

}
