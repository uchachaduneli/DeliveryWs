package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.dao.ChekInOutDao;
import ge.bestline.delivery.ws.dto.CourierCheckInOutDTO;
import ge.bestline.delivery.ws.dto.WaybillDTO;
import ge.bestline.delivery.ws.repositories.ChekInOutRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/checkInOut")
@AllArgsConstructor
public class CheckInOutController {
    private final ChekInOutRepository repo;
    private final ChekInOutDao dao;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int rowCount,
            CourierCheckInOutDTO srchParams) throws ParseException {

        if (StringUtils.isNotBlank(srchParams.getStrOperationTime())) {
            srchParams.setOperationTime(WaybillDTO.convertStrDateToDateObj(srchParams.getStrOperationTime()));
        }
        if (StringUtils.isNotBlank(srchParams.getStrOperationTimeTo())) {
            srchParams.setOperationTimeTo(WaybillDTO.convertStrDateToDateObj(srchParams.getStrOperationTimeTo()));
        }
        if (StringUtils.isNotBlank(srchParams.getStrCreatedTime())) {
            srchParams.setCreateTimeTo(WaybillDTO.convertStrDateToDateObj(srchParams.getStrCreatedTime()));
        }
        if (StringUtils.isNotBlank(srchParams.getStrCreatedTimeTo())) {
            srchParams.setCreateTimeTo(WaybillDTO.convertStrDateToDateObj(srchParams.getStrCreatedTimeTo()));
        }
        if (srchParams.getIsCheckinParam() != null) {
            srchParams.setChekIn(srchParams.getIsCheckinParam().equals(0) ? false : srchParams.getIsCheckinParam().equals(1) ? true : null);
        }

        return new ResponseEntity<>(dao.findAll(page, rowCount, srchParams), HttpStatus.OK);
    }
}
