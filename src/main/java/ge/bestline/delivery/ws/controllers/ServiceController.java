package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.entities.Services;
import ge.bestline.delivery.ws.repositories.ServicesRepository;
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
@RequestMapping(path = "/service")
public class ServiceController {

    private final ServicesRepository repo;

    public ServiceController(ServicesRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    @Transactional
    public Services addNew(@RequestBody Services obj) {
        log.info("Adding New Service: " + obj.toString());
        return repo.save(obj);
    }

    @PostMapping(path = "/{id}")
    @Transactional
    public ResponseEntity<Services> updateById(@PathVariable Integer id, @RequestBody Services request) {
        log.info("Updating Services");
        Services existing = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : " + id));
        log.info("Old Values: " + existing.toString() + "    New Values: " + request.toString());
        existing.setName(request.getName());
        Services updatedObj = repo.save(existing);
        return ResponseEntity.ok(updatedObj);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Map<String, Boolean>> delete(@PathVariable Integer id) {
        Services existing = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : " + id));
        log.info("Deleting Services: " + existing.toString());
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
            Services searchParams) {
        Map<String, Object> resp = new HashMap<>();
        Pageable paging = PageRequest.of(page, rowCount, Sort.by("id").descending());
        Page<Services> pageAuths = null;
        if (searchParams.getName() != null) {
            pageAuths = repo.findByName(searchParams.getName(), paging);
        } else {
            pageAuths = repo.findAll(paging);
        }
        resp.put("items", pageAuths.getContent());
        resp.put("total_count", pageAuths.getTotalElements());
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public Services getById(@PathVariable Integer id) {
        log.info("Getting Service With ID: " + id);
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID"));
    }

}
