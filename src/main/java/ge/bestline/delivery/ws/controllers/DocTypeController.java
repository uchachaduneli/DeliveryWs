package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.entities.DocType;
import ge.bestline.delivery.ws.repositories.DocTypeRepository;
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
@RequestMapping(path = "/doctype")
public class DocTypeController {

    private final DocTypeRepository repo;

    public DocTypeController(DocTypeRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    @Transactional
    public DocType addNew(@RequestBody DocType obj) {
        log.info("Adding New DocType: " + obj.toString());
        return repo.save(obj);
    }

    @PostMapping(path = "/{id}")
    @Transactional
    public ResponseEntity<DocType> updateById(@PathVariable Integer id, @RequestBody DocType request) {
        log.info("Updating Doc Type");
        DocType existing = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : " + id));
        log.info("Old Values: " + existing.toString() + "    New Values: " + request.toString());
        existing.setName(request.getName());
        DocType updatedObj = repo.save(existing);
        return ResponseEntity.ok(updatedObj);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Map<String, Boolean>> delete(@PathVariable Integer id) {
        DocType existing = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : " + id));
        log.info("Deleting Doc Type: " + existing.toString());
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
            DocType searchParams) {
        Map<String, Object> resp = new HashMap<>();
        Pageable paging = PageRequest.of(page, rowCount, Sort.by("deleted").descending());
        Page<DocType> pageAuths = null;
        pageAuths = repo.findAll(paging);
        resp.put("items", pageAuths.getContent());
        resp.put("total_count", pageAuths.getTotalElements());
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Map<String, Boolean>> getById(@PathVariable Integer id) {
        DocType existing = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : " + id));
        log.info("Deleting DocType: " + existing.toString());
        existing.setDeleted(1);
        repo.save(existing);
        Map<String, Boolean> resp = new HashMap<>();
        resp.put("deleted", Boolean.TRUE);
        return ResponseEntity.ok(resp);
    }

}
