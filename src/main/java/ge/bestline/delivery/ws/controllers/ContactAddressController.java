package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.dao.ContactAddressDao;
import ge.bestline.delivery.ws.entities.ContactAddress;
import ge.bestline.delivery.ws.repositories.CityRepository;
import ge.bestline.delivery.ws.repositories.ContactAddressRepository;
import ge.bestline.delivery.ws.repositories.ContactRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping(path = "/contactAddress")
public class ContactAddressController {

    private final ContactAddressRepository repo;
    private final CityRepository cityRepository;
    private final ContactRepository contactRepository;
    private final ContactAddressDao contactAddressDao;

    public ContactAddressController(ContactAddressRepository repo, CityRepository cityRepository,
                                    ContactRepository contactRepository, ContactAddressDao contactAddressDao) {
        this.repo = repo;
        this.cityRepository = cityRepository;
        this.contactRepository = contactRepository;
        this.contactAddressDao = contactAddressDao;
    }

    @PostMapping
    @Transactional
    public ContactAddress addNew(@RequestBody ContactAddress obj) {
        log.info("Adding New ContactAddress: " + obj.toString());
        return repo.save(obj);
    }

    @PostMapping(path = "/{id}")
    @Transactional
    public ResponseEntity<ContactAddress> updateById(@PathVariable Integer id, @RequestBody ContactAddress request) {
        log.info("Updating ContactAddress");
        ContactAddress existing = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : " + id));
        log.info("Old Values: " + existing.toString() + "    New Values: " + request.toString());
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
        log.info("Deleting Tranzit: " + existing.toString());
        repo.delete(existing);
        Map<String, Boolean> resp = new HashMap<>();
        resp.put("deleted", Boolean.TRUE);
        return ResponseEntity.ok(resp);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(ContactAddress searchParams) {
        log.info("Getting Contact Addresses with params: " + searchParams);
        return new ResponseEntity<>(contactAddressDao.findAll(searchParams), HttpStatus.OK);
    }

    @GetMapping(path = "contact/{contactId}")
    public Iterable<ContactAddress> getByContactId(@PathVariable Integer contactId) {
        log.info("Getting Contact Address With Contact ID: " + contactId);
        return repo.findByContact_Id(contactId);
    }

}
