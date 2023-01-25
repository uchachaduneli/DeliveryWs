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
import java.util.List;
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
        //determining mainAddress existence into companys existing addresses
        List<ContactAddress> existings = repo.findByContact_Id(obj.getContact().getId());
        if (existings.isEmpty()) {// this is first address, main will be it
            obj.setIsPayAddress(1);
        } else {
            if (obj.getIsPayAddress() == 1) {// new one is requested as main address but there are existing ones, make them nonMain
                for (ContactAddress ca : existings) {
                    ca.setIsPayAddress(2);
                }
                repo.saveAll(existings);
            } else { // new one is requested as nonMain address but there are existing ones, make new one as MainAddress
                boolean mainAddressFound = false;
                for (ContactAddress ca : existings) {
                    if (ca.getIsPayAddress() != null && ca.getIsPayAddress() == 1) {
                        mainAddressFound = true;
                        break;
                    }
                }
                if (!mainAddressFound) {
                    obj.setIsPayAddress(1);
                }
            }
        }
        return repo.save(obj);
    }

    @PostMapping(path = "/{id}")
    @Transactional
    public ResponseEntity<ContactAddress> updateById(@PathVariable Integer id, @RequestBody ContactAddress request) {
        log.info("Updating ContactAddress");
        ContactAddress existing = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : " + id));
        log.info("Old Values: " + existing.toString() + "    New Values: " + request.toString());

        if (request.getIsPayAddress() != existing.getIsPayAddress()) {
            List<ContactAddress> existings = repo.findByContact_Id(request.getContact().getId());
            if (request.getIsPayAddress() == 1) {
                for (ContactAddress ca : existings) {
                    ca.setIsPayAddress(2);
                }
                repo.saveAll(existings);
            } else {
                existing.setIsPayAddress(request.getIsPayAddress());
                boolean mainAddressFound = false;
                for (ContactAddress ca : existings) {
                    if (ca.getIsPayAddress() != null && ca.getIsPayAddress() == 1) {
                        mainAddressFound = true;
                        break;
                    }
                }
                if (!mainAddressFound) {
                    existing.setIsPayAddress(1);
                }
            }
        }

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
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int rowCount,
            ContactAddress searchParams) {
        log.info("Getting Contact Addresses with params: " + searchParams);
        return new ResponseEntity<>(contactAddressDao.findAll(page, rowCount, searchParams), HttpStatus.OK);
    }

    @GetMapping(path = "contact/{contactId}")
    public List<ContactAddress> getByContactId(@PathVariable Integer contactId) {
        log.info("Getting Contact Address With Contact ID: " + contactId);
        return repo.findByContact_Id(contactId);
    }

}
