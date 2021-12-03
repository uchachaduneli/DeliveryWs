package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.entities.Tariff;
import ge.bestline.delivery.ws.entities.TariffDetail;
import ge.bestline.delivery.ws.repositories.CityRepository;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping(path = "/tariff")
public class TariffController {

    private final TariffRepository repo;
    private final TariffDetailsRepository repoDetails;
    private final ZoneRepository repoZone;
    private final CityRepository cityRepo;

    public TariffController(TariffRepository repo, TariffDetailsRepository repoDetails,
                            ZoneRepository repoZone, CityRepository cityRepo) {
        this.repo = repo;
        this.repoDetails = repoDetails;
        this.repoZone = repoZone;
        this.cityRepo = cityRepo;
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

    @PostMapping("/detailsList")
    @Transactional
    public ResponseEntity<ArrayList<TariffDetail>> addNewTariffDetailsList(@RequestBody ArrayList<TariffDetail> list) {
        log.info("Adding tariff Details data: " + list.toString());
        ArrayList<TariffDetail> res = new ArrayList<>();
        for (TariffDetail det : list) {
            res.add(repoDetails.save(det));
        }
        return ResponseEntity.ok(res);
    }

    @PutMapping("/detailsList")
    @Transactional
    public ResponseEntity<ArrayList<TariffDetail>> updateTariffDetailsList(@RequestBody ArrayList<TariffDetail> list) {
        log.info("updating tariff Details data: " + list.toString());
        ArrayList<TariffDetail> res = new ArrayList<>();
        for (TariffDetail det : list) {
            res.add(repoDetails.save(det));
        }
        return ResponseEntity.ok(res);
    }

    @GetMapping(path = "/calculatePrice/{tariffId}/{zoneId}/{weight}")
    @Transactional
    public ResponseEntity<Double> calculatePrice(
            @PathVariable Integer tariffId,
            @PathVariable Integer zoneId,
            @PathVariable Double weight) {
        // try to find price with exact weight
        List<TariffDetail> details = repoDetails.findByTariffIdAndZoneIdAndWeight(tariffId, zoneId, weight);
        if (details.isEmpty()) {
            //  price with exact weight not fount trying to find first price with greater weight
            details = repoDetails.findByTariffIdAndZoneIdAndWeightGreaterThanOrderByWeightAsc(tariffId, zoneId, weight);
            if (details.isEmpty()) {
                log.error("Can't get Price from TariffDetails Using This IDes {tariffId}/{zoneId}/{weight} : "
                        + tariffId + "/" + zoneId + "/" + weight);
                throw new ResourceNotFoundException("Can't get Price from TariffDetails Using This IDes {tariffId}/{zoneId}/{weight} : "
                        + tariffId + "/" + zoneId + "/" + weight);
            }
            return ResponseEntity.ok(details.get(0).getPrice());
        } else {
            return ResponseEntity.ok(details.get(0).getPrice());
        }
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
