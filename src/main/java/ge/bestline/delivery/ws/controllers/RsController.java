package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.dao.RsDao;
import ge.bestline.delivery.ws.entities.WayBill;
import ge.bestline.delivery.ws.repositories.TranporterWaybillRepository;
import ge.bestline.delivery.ws.services.RsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/rs")
public class RsController {
    private final RsService rsService;
    private final RsDao dao;
    private final TranporterWaybillRepository repo;

    public RsController(RsService rsService,
                        RsDao dao,
                        TranporterWaybillRepository repo) {
        this.rsService = rsService;
        this.dao = dao;
        this.repo = repo;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int rowCount,
            WayBill searchParams) {
        return new ResponseEntity<>(dao.findAll(page, rowCount, searchParams), HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public WayBill getWayBillById(@PathVariable Integer id) {
        log.info("Getting Zone With ID: " + id);
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID"));
    }

}
