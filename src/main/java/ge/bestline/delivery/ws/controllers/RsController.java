package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.dao.WaybillDao;
import ge.bestline.delivery.ws.dto.WaybillDTO;
import ge.bestline.delivery.ws.entities.WayBill;
import ge.bestline.delivery.ws.repositories.TranporterWaybillRepository;
import ge.bestline.delivery.ws.services.RsService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/rs")
public class RsController {
    private final RsService rsService;
    private final WaybillDao dao;
    private final TranporterWaybillRepository repo;

    public RsController(RsService rsService,
                        WaybillDao dao,
                        TranporterWaybillRepository repo) {
        this.rsService = rsService;
        this.dao = dao;
        this.repo = repo;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int rowCount,
            WaybillDTO srchParams) throws ParseException {

        if (StringUtils.isNotBlank(srchParams.getStrRsCreateDate())) {
            srchParams.setRsCreateDate(WaybillDTO.convertStrDateToDateObj(srchParams.getStrRsCreateDate()));
        }
        if (StringUtils.isNotBlank(srchParams.getStrRsCreateDateTo())) {
            srchParams.setRsCreateDateTo(WaybillDTO.convertStrDateToDateObj(srchParams.getStrRsCreateDateTo()));
        }

        return new ResponseEntity<>(dao.findAll(page, rowCount, srchParams), HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public WayBill getWayBillById(@PathVariable Integer id) {
        log.info("Getting Zone With ID: " + id);
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID"));
    }

}
