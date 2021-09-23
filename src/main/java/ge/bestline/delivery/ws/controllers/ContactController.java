package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.dao.ContactDao;
import ge.bestline.delivery.ws.entities.Contact;
import ge.bestline.delivery.ws.repositories.ContactRepository;
import ge.bestline.delivery.ws.repositories.UserRepository;
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
@RequestMapping(path = "/contact")
public class ContactController {

    private final ContactRepository repo;
    private final ContactDao dao;
    private final UserRepository userRepository;

    public ContactController(ContactRepository repo, ContactDao dao, UserRepository userRepository) {
        this.repo = repo;
        this.dao = dao;
        this.userRepository = userRepository;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        return new ResponseEntity<>("თქვენ უკვე გყავთ მითითებული საიდენტიფიკაციო ნომრით კონტაქტი", HttpStatus.BAD_REQUEST);
    }

    @PostMapping
    @Transactional
    public Contact addNew(@RequestBody Contact obj) {
        log.info("Adding New Contact: " + obj.toString());
        return repo.save(obj);
    }

    @PostMapping(path = "/{id}")
    @Transactional
    public ResponseEntity<Contact> updateById(@PathVariable Integer id, @RequestBody Contact request) {
        log.info("Updating Contact");
        Contact existing = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : " + id));
        log.info("Old Values: " + existing.toString() + "    New Values: " + request.toString());
        existing.setName(request.getName());
        existing.setEmail(request.getEmail());
        existing.setDeReGe(request.getDeReGe());
        existing.setIdentNumber(request.getIdentNumber());
        existing.setStatus(request.getStatus());
        existing.setType(request.getType());
        existing.setHasContract(request.getHasContract());
        existing.setUser(userRepository.findById(request.getUser().getId()).orElseThrow(() ->
                new ResourceNotFoundException("Can't find User Using This ID : " + request.getUser().getId())));
        Contact updatedObj = repo.save(existing);
        return ResponseEntity.ok(updatedObj);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Map<String, Boolean>> delete(@PathVariable Integer id) {
        Contact existing = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : " + id));
        log.info("Deleting Contact: " + existing.toString());
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
            Contact searchParams) {
        return new ResponseEntity<>(dao.findAll(page, rowCount, searchParams), HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public Contact getById(@PathVariable Integer id) {
        log.info("Getting Contact With ID: " + id);
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID"));
    }

}
