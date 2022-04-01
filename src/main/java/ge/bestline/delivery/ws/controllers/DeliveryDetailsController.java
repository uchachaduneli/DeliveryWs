package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.dao.DeliveryDetailDao;
import ge.bestline.delivery.ws.entities.Contact;
import ge.bestline.delivery.ws.entities.ContactAddress;
import ge.bestline.delivery.ws.entities.DeliveryDetail;
import ge.bestline.delivery.ws.repositories.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping(path = "/deliveryDetails")
public class DeliveryDetailsController {

    private final DeliveryDetailRepository repo;
    private final UserRepository userRepository;
    private final ContactRepository contactRepository;
    private final DeliveryDetailDao dao;

    public DeliveryDetailsController(DeliveryDetailRepository repo,
                                     UserRepository userRepository,
                                     ContactRepository contactRepository,
                                     DeliveryDetailDao dao) {
        this.repo = repo;
        this.userRepository = userRepository;
        this.contactRepository = contactRepository;
        this.dao = dao;
    }

    @PostMapping(path = "/{id}")
    @Transactional
    public ResponseEntity<DeliveryDetail> updateById(@PathVariable Integer id, @RequestBody DeliveryDetail request) {
        log.info("Updating DeliveryDetail");
        DeliveryDetail existing = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : " + id));
        log.info("Old Values: " + existing.toString() + "    New Values: " + request.toString());

        DeliveryDetail updatedObj = repo.save(existing);
        return ResponseEntity.ok(updatedObj);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int rowCount,
            DeliveryDetail searchParams) {
        log.info("Getting DeliveryDetails with params: " + searchParams);
        return new ResponseEntity<>(dao.findAll(page, rowCount, searchParams), HttpStatus.OK);
    }
}
