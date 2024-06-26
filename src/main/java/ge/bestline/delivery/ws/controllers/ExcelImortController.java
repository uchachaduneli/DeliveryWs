package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.dao.ExcelTmpParcelDao;
import ge.bestline.delivery.ws.dto.ResponseMessage;
import ge.bestline.delivery.ws.dto.StatusReasons;
import ge.bestline.delivery.ws.dto.TokenUser;
import ge.bestline.delivery.ws.entities.*;
import ge.bestline.delivery.ws.repositories.*;
import ge.bestline.delivery.ws.security.jwt.JwtTokenProvider;
import ge.bestline.delivery.ws.services.BarCodeService;
import ge.bestline.delivery.ws.services.CityService;
import ge.bestline.delivery.ws.services.FilesStorageService;
import ge.bestline.delivery.ws.services.PriceService;
import ge.bestline.delivery.ws.util.ExcelHelper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@RestController
@RequestMapping(path = "/excel")
public class ExcelImortController {

    private final FilesStorageService storageService;
    private final UserRepository userRepo;
    private final ParcelRepository parcelRepo;
    private final BarCodeService barCodeService;
    private final ContactRepository contactRepo;
    private final RouteRepository routeRepo;
    private final DocTypeRepository docTypeRepo;
    private final ExcelTmpParcelDao dao;
    private final CityRepository cityRepo;
    private final JwtTokenProvider jwtTokenProvider;
    private final ExcelTmpParcelRepository repo;
    private final ParcelStatusReasonRepository statusReasonRepo;
    private final ContactAddressRepository contactAddressRepo;
    private final ServicesRepository servicesRepository;
    private final ExcelHelper excelHelper;
    private final PriceService priceService;
    private final CityService cityService;
    private final ParcelStatusHistoryRepo statusHistoryRepo;

    public ExcelImortController(FilesStorageService storageService,
                                UserRepository userRepo,
                                ParcelRepository parcelRepo,
                                BarCodeService barCodeService, ContactRepository contactRepo,
                                RouteRepository routeRepo,
                                DocTypeRepository docTypeRepo,
                                ExcelTmpParcelDao dao,
                                CityRepository cityRepo, JwtTokenProvider jwtTokenProvider, ExcelTmpParcelRepository repo,
                                ParcelStatusReasonRepository statusReasonRepo, ContactAddressRepository contactAddressRepo,
                                ServicesRepository servicesRepository,
                                ExcelHelper excelHelper,
                                PriceService priceService, CityService cityService, ParcelStatusHistoryRepo statusHistoryRepo) {
        this.storageService = storageService;
        this.userRepo = userRepo;
        this.parcelRepo = parcelRepo;
        this.barCodeService = barCodeService;
        this.contactRepo = contactRepo;
        this.routeRepo = routeRepo;
        this.docTypeRepo = docTypeRepo;
        this.dao = dao;
        this.cityRepo = cityRepo;
        this.jwtTokenProvider = jwtTokenProvider;
        this.repo = repo;
        this.statusReasonRepo = statusReasonRepo;
        this.contactAddressRepo = contactAddressRepo;
        this.servicesRepository = servicesRepository;
        this.excelHelper = excelHelper;
        this.priceService = priceService;
        this.cityService = cityService;
        this.statusHistoryRepo = statusHistoryRepo;
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        return new ResponseEntity<>("ჩანაწერი მსგავსი ბარკოდით უკვე არსებობს", HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/move-to-main")
    @Transactional
    public ResponseEntity<List<Parcel>> moveToMainTable(
            @RequestParam(value = "senderIdentNum", required = true) String senderIdentNum,
            HttpServletRequest req) {
        TokenUser requester = jwtTokenProvider.getRequesterUserData(req);
        log.info("moving exel imported rows to parcels main table " + requester.getName() + " " + requester.getLastName());
        List<ExcelTmpParcel> usersImportedParcels = repo.findBySenderIdentNumber(senderIdentNum);
        List<Parcel> res = new ArrayList<>();
        List<ParcelStatusHistory> statusHistories = new ArrayList<>();
        ParcelStatusReason status = statusReasonRepo.findById(StatusReasons.PP.getStatus().getId()).orElseThrow(() ->
                new ResourceNotFoundException("Can't find StatusReason Record with PP - enum's value"));
        for (ExcelTmpParcel obj : usersImportedParcels) {
            ContactAddress conAdrs = null;
            try {
                conAdrs = contactAddressRepo.findByIsPayAddress(1);
                if (conAdrs == null) {
                    throw new RuntimeException("Can't Find PayAddress For Contact " + obj.getSender().getIdentNumber());
                }
            } catch (RuntimeException e) {
                log.warn(e.getMessage());
                conAdrs = contactAddressRepo.findFirstByContact_Id(obj.getSender().getId());
            } catch (Exception e) {
                log.error(e);
            }
            res.add(new Parcel(obj));

        }
        res = parcelRepo.saveAll(res);
        for (Parcel p : res) {
            statusHistories.add(new ParcelStatusHistory(
                    p,
                    status.getStatus().getName(),
                    status.getStatus().getCode(),
                    status.getName(),
                    new Timestamp(new Date().getTime()),
                    new User(requester.getId())));
        }
        statusHistoryRepo.saveAll(statusHistories);
        repo.deleteAll(usersImportedParcels);
        String barcodes = res.stream().map(Parcel::getBarCode).collect(Collectors.joining(","));
        log.info("Excel Imported Rows With These BarCodes Has Been Moved To Parcels Main Table :" + barcodes);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/import")
    @Transactional
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file,
                                                      @RequestParam(value = "senderId", required = false) Integer senderId,
                                                      @RequestParam(value = "routeId", required = false) Integer routeId,
                                                      @RequestParam(value = "authorId", required = true) Integer authorId,
                                                      @RequestParam(value = "cityId", required = true) Integer fromCityId,
                                                      @RequestParam(value = "address", required = true) String address,
                                                      @RequestParam(value = "contactPerson", required = true) String contactPerson,
                                                      @RequestParam(value = "contactPhone", required = true) String contactPhone,
                                                      @RequestParam(value = "serviceId", required = true) Integer serviceId) {
        log.info("Started Importing Excel Into Tmp Table");
        String message;
        try {
            Contact senderContact = contactRepo.findById(senderId).orElseThrow(() -> new ResourceNotFoundException("Can't find Sender Contact Using This ID=" + senderId));
            Route route = routeRepo.findById(routeId).orElseThrow(() -> new ResourceNotFoundException("Can't find Route Using This ID=" + routeId));
            User author = userRepo.findByPersonalNumber(senderContact.getIdentNumber());
            if (author == null) {
                //if sender conpany has no user account into system just set the excel importer users as author
                author = userRepo.findById(authorId).orElseThrow(() -> new ResourceNotFoundException("Can't find User Using This ID : " + authorId));
            }
            City senderCity = cityRepo.findById(fromCityId).orElseThrow(() -> new ResourceNotFoundException("Can't find From City Using This ID : " + fromCityId));
            Services service = servicesRepository.findById(serviceId).orElseThrow(() -> new ResourceNotFoundException("Can't find Service Using This ID : " + serviceId));
            List<ExcelTmpParcel> parsedRowsList = storageService.convertExcelToParcelList(file);
            Date uploadDate = new Date();
            Long perImportJoningId = uploadDate.getTime();// joining id for per excell file's rows
            List<String> newBarCodes = barCodeService.getBarcodes(parsedRowsList.size());
            for (int i = 0; i < parsedRowsList.size(); i++) {
                ExcelTmpParcel tmpObj = parsedRowsList.get(i);
                tmpObj.setAuthor(author);
                tmpObj.setBarCode(newBarCodes.get(i));
                tmpObj.setCreatedTime(uploadDate);
                tmpObj.setSender(senderContact);
                tmpObj.setRoute(route);
                tmpObj.setSenderCity(senderCity);
                tmpObj.setSenderAddress(address);
                tmpObj.setSenderContactPerson(contactPerson);
                tmpObj.setSenderPhone(contactPhone);
                tmpObj.setTmpIdForPerExcel(perImportJoningId);
                tmpObj.setService(service);
                City receiverCity = cityRepo.findById(tmpObj.getReceiverCity().getId()).orElseThrow(() ->
                        new RuntimeException("Can't find City using this id" + tmpObj.getReceiverCity().getId() + " At Row " + tmpObj.getRowIndex()));
                City cityForZone = cityService.getLongestDestZone(senderCity.getId(), receiverCity.getId());
                tmpObj.setTotalPrice(priceService.calculatePrice(service.getId(),
                        senderContact.getTariff() != null ? senderContact.getTariff().getId() : 1,
                        cityForZone.getZone().getId(), tmpObj.getWeight()));
                tmpObj.setReceiverCity(receiverCity);
                repo.save(tmpObj);

            }
            message = "File Imported Successfully";
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        } catch (ResourceNotFoundException e) {
            log.error(e);
            message = "Could not process Excel import: " + file.getOriginalFilename() + "! " + e.getMessage();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        } catch (RuntimeException e) {
            log.error(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage(e.getMessage()));
        } catch (Exception e) {
            log.error(e);
            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int rowCount,
            ExcelTmpParcel searchParams) {
        log.info("Retrieving Excel imported Tmp Parcels Records");
        return new ResponseEntity<>(dao.findAll(page, rowCount, searchParams), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Map<String, Boolean>> delete(@PathVariable Integer id) {
        ExcelTmpParcel existing = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : " + id));
        log.info("Deleting ExcelTmpParcel: " + existing.toString());
        repo.delete(existing);
        Map<String, Boolean> resp = new HashMap<>();
        resp.put("deleted", Boolean.TRUE);
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/all")
    @Transactional
    public ResponseEntity<Map<String, Boolean>> deleteAll(HttpServletRequest req) throws ParseException {
        TokenUser requester = jwtTokenProvider.getRequesterUserData(req);
        List<ExcelTmpParcel> existings = repo.findAll();
        log.info("Deleting All Tmp Excel Imports, Operating User Is: " + requester.getName() + " " + requester.getLastName());
        repo.deleteAll(existings);
        Map<String, Boolean> resp = new HashMap<>();
        resp.put("deleted", Boolean.TRUE);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/excel")
    public ResponseEntity<Resource> downloadExcell(Integer userId) {
        log.info("Excel Generation & Download Started ");
        try {
            InputStreamResource file = new InputStreamResource(excelHelper.importedExcelRowsToExcelFile(repo.findByAuthorId(userId)));
            log.info("Excel Generation Finished, Returning The File");
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=importedParcels.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(file);
        } catch (Exception ex) {
            log.error("Error Occurred During Excel Generation", ex);
            return new ResponseEntity<Resource>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
