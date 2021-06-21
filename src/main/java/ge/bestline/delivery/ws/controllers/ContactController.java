package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.entities.Contact;
import ge.bestline.delivery.ws.entities.ContactAddress;
import ge.bestline.delivery.ws.repositories.ContactAddressRepository;
import ge.bestline.delivery.ws.repositories.ContactRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/contact")
@CrossOrigin(origins = "http://localhost:4200")
public class ContactController {

    private final ContactRepository repo;
    private final ContactAddressRepository contactAddressRepository;

    public ContactController(ContactRepository repo, ContactAddressRepository contactAddressRepository) {
        this.repo = repo;
        this.contactAddressRepository = contactAddressRepository;
    }

    @GetMapping
    public Iterable<Contact> getAll() {
        return repo.findAll();
    }

    @GetMapping(path = "/{id}")
    public Contact getById(@PathVariable Integer id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID"));
    }

    @GetMapping(path = "/addresses/{id}")
    public Iterable<ContactAddress> getContactAddressesById(@PathVariable Integer id) {
        return contactAddressRepository.findByContact(id);
    }

}
