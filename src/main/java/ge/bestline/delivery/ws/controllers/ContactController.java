package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.entities.Contact;
import ge.bestline.delivery.ws.repositories.ContactRepository;
import ge.bestline.delivery.ws.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/contact")
@CrossOrigin(origins = "http://localhost:4200")
public class ContactController {

    private final ContactRepository repo;
    private final UserRepository userRepository;

    public ContactController(ContactRepository repo, UserRepository userRepository) {
        this.repo = repo;
        this.userRepository = userRepository;
    }

    @PostMapping
    @Transactional
    public Contact addNew(@RequestBody Contact obj) {
        return repo.save(obj);
    }

    @PostMapping(path = "/{id}")
    @Transactional
    public ResponseEntity<Contact> updateById(@PathVariable Integer id, @RequestBody Contact request) {
        Contact existing = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : " + id));
        existing.setName(request.getName());
        existing.setEmail(request.getEmail());
        existing.setDeReGe(request.getDeReGe());
        existing.setIdentNumber(request.getIdentNumber());
        existing.setStatus(request.getStatus());
        existing.setType(request.getType());
        existing.setUser(userRepository.findById(request.getUser().getId()).orElseThrow(() ->
                new ResourceNotFoundException("Can't find User Using This ID : " + request.getUser().getId())));
        Contact updatedObj = repo.save(existing);
        return ResponseEntity.ok(updatedObj);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Map<String, Boolean>> delete(@PathVariable Integer id) {
        Contact existing = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : " + id));
        existing.setDeleted(1);
        repo.save(existing);
        Map<String, Boolean> resp = new HashMap<>();
        resp.put("deleted", Boolean.TRUE);
        return ResponseEntity.ok(resp);
    }

    @GetMapping
    public Iterable<Contact> getAll() {
        return repo.findAll();
    }

    @GetMapping(path = "/{id}")
    public Contact getById(@PathVariable Integer id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID"));
    }

}
