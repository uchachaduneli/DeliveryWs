package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.Exception.WaybillException;
import ge.bestline.delivery.ws.dao.WaybillDao;
import ge.bestline.delivery.ws.dto.WaybillDTO;
import ge.bestline.delivery.ws.entities.WayBill;
import ge.bestline.delivery.ws.repositories.TransporterWaybillRepository;
import ge.bestline.delivery.ws.services.RsService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.datatype.DatatypeConfigurationException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/rs")
public class RsController {
    private final RsService rsService;
    private final WaybillDao dao;
    private final TransporterWaybillRepository repo;

    public RsController(RsService rsService,
                        WaybillDao dao,
                        TransporterWaybillRepository repo) {
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

    @GetMapping(path = "closeWaybill/{barCode}")
    public ResponseEntity<Map<String, String>> closeWayBillByBarCode(@PathVariable String barCode) {
        log.info("closing Waybill With barCode: " + barCode);
        Map<String, String> res = new HashMap<>();
        try {
            rsService.closeRsWaybill(barCode);
            res.put("status", "ok");
        } catch (WaybillException e) {
            log.error("Can't Close Waybill Rs Service Returned With Error " + e.getMessage());
            res.put("status", "failed");
            res.put("reason", e.getMessage());
        } catch (NumberFormatException e) {
            log.error("Can't Close Waybill", e);
            res.put("status", "failed");
            res.put("reason", e.getMessage());
        } catch (DatatypeConfigurationException e) {
            log.error("Can't Close Waybill, error occured before Rs calling ", e);
            res.put("status", "failed");
            res.put("reason", e.getMessage());
        }
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}
