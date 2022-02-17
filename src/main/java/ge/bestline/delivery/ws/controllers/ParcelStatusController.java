package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.dto.ParcelStatusWithReasonsDTO;
import ge.bestline.delivery.ws.entities.ParcelStatus;
import ge.bestline.delivery.ws.entities.ParcelStatusReason;
import ge.bestline.delivery.ws.repositories.ParcelStatusReasonRepository;
import ge.bestline.delivery.ws.repositories.ParcelStatusRepository;
import ge.bestline.delivery.ws.util.ExcelHelper;
import lombok.extern.log4j.Log4j2;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping(path = "/parcelStatus")
public class ParcelStatusController {

    private final ParcelStatusRepository repo;
    private final ParcelStatusReasonRepository statusReasonRepo;
    private final ExcelHelper excelHelper;

    public ParcelStatusController(ParcelStatusRepository repo,
                                  ParcelStatusReasonRepository statusReasonRepo,
                                  ExcelHelper excelHelper) {
        this.repo = repo;
        this.statusReasonRepo = statusReasonRepo;
        this.excelHelper = excelHelper;
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        return new ResponseEntity<>("მსგავსი სტატუსი უკვე არსებობს", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/excel")
    public ResponseEntity<Resource> downloadExcell(ParcelStatus searchParams) {
        log.info("Excel Generation & Download Started ");
        try {
            List<ParcelStatusWithReasonsDTO> excelList = new ArrayList<>();
            List<ParcelStatus> statuses = repo.findAll();// filter with searchParam
            statuses.forEach(status -> {
                excelList.add(new ParcelStatusWithReasonsDTO(status, statusReasonRepo.findByStatus_Id(status.getId())));
            });
            InputStreamResource file = new InputStreamResource(excelHelper.parcelStatusesWithReasonsToExcelFile(excelList));
            log.info("Excel Generation Finished, Returning The File");
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=checkpoints.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(file);
        } catch (Exception ex) {
            log.error("Error Occurred During Excel Generation", ex);
            return new ResponseEntity<Resource>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
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

    @PostMapping("/statusReason")
    @Transactional
    public ParcelStatusReason addNew(@RequestBody ParcelStatusReason obj) {
        log.info("Adding New Parcel Status Status: " + obj.toString());
        return statusReasonRepo.save(obj);
    }

    @GetMapping(path = "/statusReason/{parcelStatusId}")
    public ResponseEntity<Map<String, Object>> getByParcelStatusId(@PathVariable Integer parcelStatusId) {
        log.info("Getting Parcel Statuse Reason With Parcel Status ID: " + parcelStatusId);
        Map<String, Object> resp = new HashMap<>();
        List<ParcelStatusReason> list = statusReasonRepo.findByStatus_Id(parcelStatusId);
        resp.put("items", list);
        resp.put("total_count", list.size());
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @GetMapping(path = "/statusReason")
    public ResponseEntity<Map<String, Object>> getStatusReasons() {
        log.info("Getting All Parcel Statuse Reasons");
        Map<String, Object> resp = new HashMap<>();
        List<ParcelStatusReason> list = statusReasonRepo.findAll();
        resp.put("items", list);
        resp.put("total_count", list.size());
        return new ResponseEntity<>(resp, HttpStatus.OK);
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
