package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.entities.Zone;
import ge.bestline.delivery.ws.repositories.ZoneRepository;
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
@RequestMapping(path = "/zone")
public class ZoneController {

    private final ZoneRepository repo;

    public ZoneController(ZoneRepository repo) {
        this.repo = repo;
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        return new ResponseEntity<>("მსგავსი ზონა უკვე არსებობს", HttpStatus.BAD_REQUEST);
    }

    @PostMapping
    @Transactional
    public Zone addNew(@RequestBody Zone obj) {
        log.info("Adding New Zone: " + obj.toString());
        return repo.save(obj);
    }

    @PostMapping(path = "/{id}")
    @Transactional
    public ResponseEntity<Zone> updateById(@PathVariable Integer id, @RequestBody Zone request) {
        log.info("Updating Zone");
        Zone existing = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : " + id));
        log.info("Old Values: " + existing.toString() + "    New Values: " + request.toString());
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
        log.info("Deleting Zone: " + existing.toString());
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
            Zone searchParams) {
        Map<String, Object> resp = new HashMap<>();
        Pageable paging = PageRequest.of(page, rowCount, Sort.by("id").descending());
        Page<Zone> pageAuths = null;
        pageAuths = repo.findAll(paging);
        resp.put("items", pageAuths.getContent());
        resp.put("total_count", pageAuths.getTotalElements());
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public Zone getZonesById(@PathVariable Integer id) {
        log.info("Getting Zone With ID: " + id);
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID"));
    }

}
