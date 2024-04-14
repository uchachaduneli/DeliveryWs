package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.entities.Bag;
import ge.bestline.delivery.ws.repositories.BagRepository;
import ge.bestline.delivery.ws.repositories.ParcelStatusReasonRepository;
import ge.bestline.delivery.ws.repositories.WarehouseRepository;
import ge.bestline.delivery.ws.services.BarCodeService;
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
@RequestMapping(path = "/bags")
public class BagController {

    private final BagRepository repo;
    private final WarehouseRepository warehouseRepo;
    private final BarCodeService barCodeService;
    private final ParcelStatusReasonRepository statusReasonRepo;

    public BagController(BagRepository repo,
                         WarehouseRepository warehouseRepo,
                         BarCodeService barCodeService, ParcelStatusReasonRepository statusReasonRepo) {
        this.repo = repo;
        this.warehouseRepo = warehouseRepo;
        this.barCodeService = barCodeService;
        this.statusReasonRepo = statusReasonRepo;
    }

    @PostMapping
    @Transactional
    public Bag addNew(@RequestBody Bag obj) {
        log.info("Adding New Bag: " + obj.toString());
        return repo.save(obj);
    }

    @PutMapping
    @Transactional
    public ResponseEntity<Bag> updateById(@RequestBody Bag request) {
        log.info("Updating Bag");
        Bag existing = repo.findById(request.getId()).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : " + request.getId()));
        log.info("Old Values: " + existing.toString() + "  New Values: " + request.toString());
        existing.setStatus(request.getStatus());
        existing.setParcels(request.getParcels());
        existing.setFrom(warehouseRepo.findById(request.getFrom().getId()).orElseThrow(() ->
                new ResourceNotFoundException("Can't find From Warehouse Using This ID : " + request.getFrom().getId())));
        existing.setTo(warehouseRepo.findById(request.getTo().getId()).orElseThrow(() ->
                new ResourceNotFoundException("Can't find To Warehouse Using This ID : " + request.getTo().getId())));
        Bag updatedObj = repo.save(existing);
        return ResponseEntity.ok(updatedObj);
    }



    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Map<String, Boolean>> delete(@PathVariable Integer id) {
        Bag existing = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : " + id));
        log.info("Deleting Bag: " + existing.toString());
        repo.delete(existing);
        Map<String, Boolean> resp = new HashMap<>();
        resp.put("deleted", Boolean.TRUE);
        return ResponseEntity.ok(resp);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int rowCount,
            Bag searchParams) {
        Map<String, Object> resp = new HashMap<>();
        Pageable paging = PageRequest.of(page, rowCount, Sort.by("id").descending());
        Page<Bag> pageAuths = null;
        pageAuths = repo.findAll(paging);
        resp.put("items", pageAuths.getContent());
        resp.put("total_count", pageAuths.getTotalElements());
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public Bag getById(@PathVariable Integer id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID"));
    }

    @GetMapping(path = "/byBarCode/{barCode}")
    public ResponseEntity<Bag> getBagByBarCode(@PathVariable String barCode) {
        log.info("Getting Bag By BarCode : " + barCode);
        Bag bag = repo.findByBarCode(barCode).orElseThrow(() -> new ResourceNotFoundException("Can't find Parcel Using This BarCode : " + barCode));
        return ResponseEntity.ok(bag);
    }

    @GetMapping("barcode")
    public ResponseEntity<String> getBarCodeForNewDetails() {
        log.info("Geting Barcode For new Bag Started");
        String barcode = barCodeService.getBarcodes(1).get(0);
        if (repo.findByBarCode(barcode).isPresent()) {
            // try to generate one more time to find not existing one in delivery details table
            barcode = barCodeService.getBarcodes(1).get(0);
        }
        return new ResponseEntity<>(barcode, HttpStatus.OK);
    }
}
