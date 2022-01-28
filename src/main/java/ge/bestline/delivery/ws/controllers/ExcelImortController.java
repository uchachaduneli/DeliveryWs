package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.dao.ExcelTmpParcelDao;
import ge.bestline.delivery.ws.dto.ResponseMessage;
import ge.bestline.delivery.ws.entities.*;
import ge.bestline.delivery.ws.repositories.*;
import ge.bestline.delivery.ws.services.BarCodeService;
import ge.bestline.delivery.ws.services.FilesStorageService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping(path = "/excel")
public class ExcelImortController {

    private final FilesStorageService storageService;
    private final UserRepository userRepo;
    private final ParcelRepository paracelRepo;
    private final BarCodeService barCodeService;
    private final ContactRepository contactRepo;
    private final RouteRepository routeRepo;
    private final DocTypeRepository docTypeRepo;
    private final ExcelTmpParcelDao dao;
    private final CityRepository cityRepo;
    private final ExcelTmpParcelRepository repo;

    public ExcelImortController(FilesStorageService storageService,
                                UserRepository userRepo,
                                ParcelRepository paracelRepo,
                                BarCodeService barCodeService, ContactRepository contactRepo,
                                RouteRepository routeRepo,
                                DocTypeRepository docTypeRepo,
                                ExcelTmpParcelDao dao,
                                CityRepository cityRepo, ExcelTmpParcelRepository repo) {
        this.storageService = storageService;
        this.userRepo = userRepo;
        this.paracelRepo = paracelRepo;
        this.barCodeService = barCodeService;
        this.contactRepo = contactRepo;
        this.routeRepo = routeRepo;
        this.docTypeRepo = docTypeRepo;
        this.dao = dao;
        this.cityRepo = cityRepo;
        this.repo = repo;
    }

    @PostMapping("/import")
//    @Transactional
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file,
                                                      @RequestParam(value = "senderId", required = false) Integer senderId,
                                                      @RequestParam(value = "routeId", required = false) Integer routeId,
                                                      @RequestParam(value = "stikerId", required = false) Integer stikerId,
                                                      @RequestParam(value = "authorId", required = true) Integer authorId) {
        log.info("Started Importing Excel Into Tmp Table");
        String message;
        try {
            Contact senderContact = contactRepo.findById(senderId).orElseThrow(() -> new ResourceNotFoundException("Can't find Sender Contact Using This ID=" + senderId));
            Route route = routeRepo.findById(routeId).orElseThrow(() -> new ResourceNotFoundException("Can't find Route Using This ID=" + routeId));
            DocType stiker = docTypeRepo.findById(stikerId).orElseThrow(() -> new ResourceNotFoundException("Can't find Stiker Using This ID=" + stikerId));
            User author = userRepo.findById(authorId).orElseThrow(() -> new ResourceNotFoundException("Can't find User Using This ID : " + authorId));
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
                tmpObj.setStiker(stiker);
                tmpObj.setTmpIdForPerExcel(perImportJoningId);
                tmpObj.setReceiverCity(cityRepo.findById(tmpObj.getReceiverCity().getId()).orElseThrow(() ->
                        new RuntimeException("Can't find City using this id" + tmpObj.getReceiverCity().getId() + " At Row " + tmpObj.getRowIndex())));
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

}
