package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.entities.ContactAddress;
import ge.bestline.delivery.ws.repositories.CityRepository;
import ge.bestline.delivery.ws.repositories.ContactAddressRepository;
import ge.bestline.delivery.ws.repositories.ContactRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/contactAddress")
@CrossOrigin(origins = "http://localhost:4200")
public class ContactAddressController {

    private final ContactAddressRepository repo;
    private final CityRepository cityRepository;
    private final ContactRepository contactRepository;

    public ContactAddressController(ContactAddressRepository repo, CityRepository cityRepository, ContactRepository contactRepository) {
        this.repo = repo;
        this.cityRepository = cityRepository;
        this.contactRepository = contactRepository;
    }

    @PostMapping
    @Transactional
    public ContactAddress addNew(@RequestBody ContactAddress obj) {
        return repo.save(obj);
    }

    @PostMapping(path = "/{id}")
    @Transactional
    public ResponseEntity<ContactAddress> updateById(@PathVariable Integer id, @RequestBody ContactAddress request) {
        ContactAddress existing = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : " + id));
        existing.setContactPerson(request.getContactPerson());
        existing.setAppartmentDetails(request.getAppartmentDetails());
        existing.setContactPersonEmail(request.getContactPersonEmail());
        existing.setContactPerson(request.getContactPerson());
        existing.setContactPersonPhone(request.getContactPersonPhone());
        existing.setPostCode(request.getPostCode());
        existing.setStreet(request.getStreet());
        existing.setContact(contactRepository.findById(request.getContact().getId()).orElseThrow(() ->
                new ResourceNotFoundException("Can't find Contact Using This ID : " + request.getContact().getId())));
        existing.setCity(cityRepository.findById(request.getCity().getId()).orElseThrow(() ->
                new ResourceNotFoundException("Can't find City Using This ID : " + request.getCity().getId())));
        ContactAddress updatedObj = repo.save(existing);
        return ResponseEntity.ok(updatedObj);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Map<String, Boolean>> delete(@PathVariable Integer id) {
        ContactAddress existing = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : " + id));
        repo.delete(existing);
        Map<String, Boolean> resp = new HashMap<>();
        resp.put("deleted", Boolean.TRUE);
        return ResponseEntity.ok(resp);
    }

    @GetMapping
    public Iterable<ContactAddress> getAll() {
        return repo.findAll();
    }

    @GetMapping(path = "/{contactId}")
    public Iterable<ContactAddress> getByContactId(@PathVariable Integer contactId) {
        return repo.findByContact_Id(contactId);
    }

}
