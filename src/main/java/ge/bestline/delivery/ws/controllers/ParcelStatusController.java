package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.entities.ParcelStatus;
import ge.bestline.delivery.ws.entities.ParcelStatusReason;
import ge.bestline.delivery.ws.repositories.ParcelStatusReasonRepository;
import ge.bestline.delivery.ws.repositories.ParcelStatusRepository;
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
@RequestMapping(path = "/parcelStatus")
public class ParcelStatusController {

    private final ParcelStatusRepository repo;
    private final ParcelStatusReasonRepository statusReasonRepo;

    public ParcelStatusController(ParcelStatusRepository repo, ParcelStatusReasonRepository statusReasonRepo) {
        this.repo = repo;
        this.statusReasonRepo = statusReasonRepo;
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        return new ResponseEntity<>("მსგავსი სტატუსი უკვე არსებობს", HttpStatus.BAD_REQUEST);
    }

    @PostMapping
    @Transactional
    public ParcelStatus addNew(@RequestBody ParcelStatus obj) {
        log.info("Adding New Parcel Status: " + obj.toString());
        return repo.save(obj);
    }

    @PostMapping(path = "/{id}")
    @Transactional
    public ResponseEntity<ParcelStatus> updateById(@PathVariable Integer id, @RequestBody ParcelStatus request) {
        log.info("Updating Parcel Status");
        ParcelStatus existing = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : " + id));
        log.info("Old Values: " + existing.toString() + "    New Values: " + request.toString());
        existing.setName(request.getName());
        existing.setCode(request.getCode());
        ParcelStatus updatedObj = repo.save(existing);
        return ResponseEntity.ok(updatedObj);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Map<String, Boolean>> delete(@PathVariable Integer id) {
        ParcelStatus existing = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : " + id));
        log.info("Deleting ParcelStatus: " + existing.toString());
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
            ParcelStatus searchParams) {
        Map<String, Object> resp = new HashMap<>();
        Pageable paging = PageRequest.of(page, rowCount, Sort.by("id").descending());
        Page<ParcelStatus> pageAuths = null;
        pageAuths = repo.findAll(paging);
        resp.put("items", pageAuths.getContent());
        resp.put("total_count", pageAuths.getTotalElements());
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @GetMapping(path = "/statusReason/{parcelStatusId}")
    public Iterable<ParcelStatusReason> getByParcelStatusId(@PathVariable Integer parcelStatusId) {
        log.info("Getting Parcel Statuse Reason With Parcel Status ID: " + parcelStatusId);
        return statusReasonRepo.findByStatus_Id(parcelStatusId);
    }

    @PutMapping(path = "/statusReason/{id}")
    @Transactional
    public ResponseEntity<ParcelStatusReason> updateParcelStatusReasonById(@PathVariable Integer id, @RequestBody ParcelStatusReason request) {
        log.info("Updating Parcel Status Reason");
        ParcelStatusReason existing = statusReasonRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : " + id));
        log.info("Old Values: " + existing.toString() + "    New Values: " + request.toString());
        existing.setName(request.getName());
        ParcelStatusReason updatedObj = statusReasonRepo.save(existing);
        return ResponseEntity.ok(updatedObj);
    }

    @DeleteMapping("/statusReason/{id}")
    @Transactional
    public ResponseEntity<Map<String, Boolean>> deleteStatusReason(@PathVariable Integer id) {
        ParcelStatusReason existing = statusReasonRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : " + id));
        log.info("Deleting Parcel Status Reason: " + existing.toString());
        statusReasonRepo.delete(existing);
        Map<String, Boolean> resp = new HashMap<>();
        resp.put("deleted", Boolean.TRUE);
        return ResponseEntity.ok(resp);
    }

}
