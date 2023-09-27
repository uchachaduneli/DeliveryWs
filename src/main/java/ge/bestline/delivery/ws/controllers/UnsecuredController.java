package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.dto.StatusReasons;
import ge.bestline.delivery.ws.entities.*;
import ge.bestline.delivery.ws.repositories.ParcelRepository;
import ge.bestline.delivery.ws.repositories.ParcelStatusHistoryRepo;
import ge.bestline.delivery.ws.repositories.ParcelStatusReasonRepository;
import ge.bestline.delivery.ws.repositories.TwoFaCodeRepository;
import ge.bestline.delivery.ws.services.BarCodeService;
import ge.bestline.delivery.ws.services.SMSService;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Data
@Log4j2
@RestController
@RequestMapping(path = "/unsec")
public class UnsecuredController {

    // static user's id from db, shouldn't be deleted from db;
    private final int TMP_CUSTOMER_USER_ID = 2;
    private final ParcelRepository repo;
    private final ParcelStatusHistoryRepo statusHistoryRepo;
    private final ParcelStatusReasonRepository statusReasonRepo;
    private final BarCodeService barCodeService;
    private final TwoFaCodeRepository twoFaCodeRepository;
    private final SMSService smsService;

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        return new ResponseEntity<>("მსგავსი ამანათი უკვე არსებობს", HttpStatus.BAD_REQUEST);
    }

    @PostMapping
    @Transactional
    public Parcel addNew(@RequestBody Parcel obj,
                         HttpServletRequest req) {
        log.info("Adding New Parcel, Usecured from Global: " + obj.toString());
        ParcelStatusReason psr = null;
        // if parcel is added from admin portal
//        if (!requester.isFromGlobalSite()) {
//            if (obj.getRoute() != null && obj.getRoute().getId() > 0) {
//                User courier = userRepository.findByRouteIdAndDeleted(obj.getRoute().getId(), 2).orElseThrow(
//                        () -> new ResourceNotFoundException("Can't find Courier Using This Route ID : " + obj.getRoute().getId()));
//                Route route = routeRepository.findByIdAndDeleted(obj.getRoute().getId(), 2).orElseThrow(
//                        () -> new ResourceNotFoundException("Can't find Route Using This ID : " + obj.getRoute().getId()));
//                obj.setCourier(courier);
//                try {
//                    if (StringUtils.isNotBlank(courier.getFirebaseToken())) {
//                        firebaseService.sendNotification(new FirebaseNote("ახალი ამანათი", "გთხოვთ იხილოთ ახალი გამოძახებების განყოფილება"), courier.getFirebaseToken());
//                    } else {
//                        log.error("Can't Send Notiff To Courier Via Firebase, Courier Token is Null or blank. -> " + courier.getFirebaseToken());
//                    }
//                } catch (Exception e) {
//                    log.error("Can't Send Notiff To Courier Via Firebase " + courier.getName() + " " + courier.getLastName() + " " + courier.getPersonalNumber(), e);
////                    throw new RuntimeException("Can't Send Notiff To Courier Via Firebase " + courier.getName() + " " + courier.getLastName() + " " + courier.getPersonalNumber(), e);
//                }
//                obj.setRoute(route);
//                psr = statusReasonRepo.findById(StatusReasons.RG.getStatus().getId()).orElseThrow(() ->
//                        new ResourceNotFoundException("Can't find parcel status reason with RG - enum's value"));
//            }
//            psr = statusReasonRepo.findById(StatusReasons.PP.getStatus().getId()).orElseThrow(() ->
//                    new ResourceNotFoundException("Can't find StatusReason Record with PP - enum's value"));
//        }
        obj.setAddedFromGlobal(true);
        psr = statusReasonRepo.findById(StatusReasons.PP.getStatus().getId()).orElseThrow(() ->
                new ResourceNotFoundException("Can't find Default StatusReason Record At ID=1 For Parcels Status History"));

        obj.setStatus(psr);
        obj.setAuthor(new User(TMP_CUSTOMER_USER_ID));
        obj.setBarCode(barCodeService.getBarcodes(1).get(0));
        Parcel parcel = repo.save(obj);
        statusHistoryRepo.save(new ParcelStatusHistory(
                parcel,
                psr.getStatus().getName(),
                psr.getStatus().getCode(),
                psr.getName(),
                new Timestamp(new Date().getTime()),
                new User(TMP_CUSTOMER_USER_ID))
        );
        return parcel;
    }

    @GetMapping(path = "/statusHistory/{id}")
    public ResponseEntity<List<ParcelStatusHistory>> getParcelStatusHistoryByParcelId(@PathVariable Integer id) {
        log.info("Getting ParcelStatusHistory By Parcel ID: " + id);
        return ResponseEntity.ok(statusHistoryRepo.findByParcelIdOrderByStatusDateTimeAsc(id));
    }

    @GetMapping(path = "/statusByBarCode/{barCode}/phone/{phone}/code/{code}")
    public ResponseEntity<String> getParcelStatusByBarCode(@PathVariable String barCode,
                                                           @PathVariable String phone,
                                                           @PathVariable String code) {
        log.info("Getting Parcels Status By BarCode : " + barCode + " Phone: " + phone + " 2Fa Code: " + code);
        try {
            TwoFaCode savedTwoFaCode = twoFaCodeRepository.findByPhoneAndCodeAndExpiredAndUsed(phone, code, false, false).
                    orElseThrow(() -> new ResourceNotFoundException("Can't find Unexpired & Unused 2Fa Code For This Phone: " + phone));
            // check if created more than 5 minutes ago
            if (new Date().getTime() - savedTwoFaCode.getCreatedTime().getTime() >= 5 * 60 * 1000) {
                // mark 2fa as Expired & return Error
                savedTwoFaCode.setExpired(true);
                twoFaCodeRepository.save(savedTwoFaCode);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Expired SMS Code");
            }
            // mark 2fa as Used
            savedTwoFaCode.setUsed(true);
            twoFaCodeRepository.save(savedTwoFaCode);
        } catch (ResourceNotFoundException r) {
            log.error(r);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Invalid SMS Code");
        }

        try {
            Parcel parcel = repo.findByBarCodeAndDeleted(barCode, 2)
                    .orElseThrow(() -> new ResourceNotFoundException("Can't find Parcel Using This BarCode: " + barCode));
            return ResponseEntity.status(HttpStatus.OK).body(parcel.getStatus().getShowInGlobal() ? parcel.getStatus().getName() : " -- ");
        } catch (ResourceNotFoundException r) {
            log.error(r);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(r.getMessage());
        }
    }

    @GetMapping(path = "/2fa/{phone}")
    public ResponseEntity<String> get2FaCode(@PathVariable String phone) {
        TwoFaCode twoFaCode = new TwoFaCode(barCodeService.generate2FaCode(), phone);
        twoFaCode = twoFaCodeRepository.save(twoFaCode);
        try {
            smsService.send(phone, twoFaCode.getCode());
        } catch (Exception e) {
            log.error("2FA SMS Sending Failed ", e);
            return ResponseEntity.internalServerError().body("SMS Sending Failed");
        }
        return ResponseEntity.ok(twoFaCode.getCode());
    }

}
