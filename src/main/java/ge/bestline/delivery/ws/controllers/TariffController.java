package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.entities.Tariff;
import ge.bestline.delivery.ws.entities.TariffDetail;
import ge.bestline.delivery.ws.repositories.TariffDetailsRepository;
import ge.bestline.delivery.ws.repositories.TariffRepository;
import ge.bestline.delivery.ws.repositories.ZoneRepository;
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
@RequestMapping(path = "/tariff")
public class TariffController {

    private final TariffRepository repo;
    private final TariffDetailsRepository repoDetails;
    private final ZoneRepository repoZone;

    public TariffController(TariffRepository repo, TariffDetailsRepository repoDetails, ZoneRepository repoZone) {
        this.repo = repo;
        this.repoDetails = repoDetails;
        this.repoZone = repoZone;
    }

    @PostMapping
    @Transactional
    public Tariff addNew(@RequestBody Tariff obj) {
        return repo.save(obj);
    }

    @PostMapping(path = "/{id}")
    @Transactional
    public ResponseEntity<Tariff> updateById(@PathVariable Integer id, @RequestBody Tariff request) {
        Tariff existing = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Tariff Using This ID : " + id));
        existing.setName(request.getName());
        Tariff updatedObj = repo.save(existing);
        return ResponseEntity.ok(updatedObj);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Map<String, Boolean>> delete(@PathVariable Integer id) {
        Tariff existing = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Tariff Using This ID : " + id));
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
            Tariff searchParams) {
        Map<String, Object> resp = new HashMap<>();
        Pageable paging = PageRequest.of(page, rowCount, Sort.by("id").descending());
        Page<Tariff> pageAuths = null;
        pageAuths = repo.findAll(paging);
        resp.put("items", pageAuths.getContent());
        resp.put("total_count", pageAuths.getTotalElements());
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public Tariff getById(@PathVariable Integer id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Tariff Using This ID"));
    }

    @GetMapping(path = "/details/{id}")
    public Iterable<TariffDetail> getTariffDetailsByTariffId(@PathVariable Integer id) {
        log.info("Getting Tariff Details With tariff ID: " + id);
        return repoDetails.findByTariff_Id(id);
    }

    @PostMapping("/details")
    @Transactional
    public TariffDetail addNewTariffDetails(@RequestBody TariffDetail obj) {
        return repoDetails.save(obj);
    }

    @PostMapping(path = "/details/{id}")
    @Transactional
    public ResponseEntity<TariffDetail> updateById(@PathVariable Integer id, @RequestBody TariffDetail request) {
        TariffDetail existing = repoDetails.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find TariffDetail Using This ID : " + id));
        existing.setZone(repoZone.findById(request.getZone().getId()).orElseThrow(() -> new ResourceNotFoundException("Can't find Zone Using This ID : " + id)));
        existing.setTariff(repo.findById(request.getTariff().getId()).orElseThrow(() -> new ResourceNotFoundException("Can't find Tariff using this ID: " + id)));
        existing.setPrice(request.getPrice());
        existing.setWeight(request.getWeight());
        TariffDetail updatedObj = repoDetails.save(existing);
        return ResponseEntity.ok(updatedObj);
    }

    @DeleteMapping("/details/{id}")
    @Transactional
    public ResponseEntity<Map<String, Boolean>> deleteDetail(@PathVariable Integer id) {
        TariffDetail existing = repoDetails.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find TariffDetail Using This ID : " + id));
        existing.setDeleted(1);
        repoDetails.save(existing);
        Map<String, Boolean> resp = new HashMap<>();
        resp.put("deleted", Boolean.TRUE);
        return ResponseEntity.ok(resp);
    }

}
