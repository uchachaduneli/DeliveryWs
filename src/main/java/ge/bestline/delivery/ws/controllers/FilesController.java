package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.dto.ResponseMessage;
import ge.bestline.delivery.ws.entities.Files;
import ge.bestline.delivery.ws.entities.Parcel;
import ge.bestline.delivery.ws.entities.User;
import ge.bestline.delivery.ws.repositories.FilesRepository;
import ge.bestline.delivery.ws.repositories.ParcelRepository;
import ge.bestline.delivery.ws.repositories.UserRepository;
import ge.bestline.delivery.ws.services.FilesStorageService;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.IOException;
import java.util.List;

@Log4j2
@RestController
@RequestMapping(path = "/files")
public class FilesController {

    private final FilesStorageService storageService;
    private final FilesRepository filesRepo;
    private final ParcelRepository parcelRepo;
    private final UserRepository userRepository;

    public FilesController(FilesStorageService storageService, FilesRepository filesRepo, ParcelRepository parcelRepo, UserRepository userRepository) {
        this.storageService = storageService;
        this.filesRepo = filesRepo;
        this.parcelRepo = parcelRepo;
        this.userRepository = userRepository;
    }

    @PostMapping("/upload")
    @Transactional
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file,
                                                      @RequestParam(value = "parcelId", required = false) Integer parcelId,
                                                      @RequestParam(value = "authorId", required = true) Integer authorId) {
        log.info("uploading file");
        User author = userRepository.findById(authorId).orElseThrow(() -> new ResourceNotFoundException("Can't find User Using This ID : " + authorId));
        String message;
        try {
            Parcel parcel;
            String uploadedFileName = storageService.save(file, parcelId);
            if (parcelId != null) {
                parcel = parcelRepo.findById(parcelId).orElseThrow(() -> new ResourceNotFoundException("Can't find Parcel Using This ID=" + parcelId));
                filesRepo.save(new Files(uploadedFileName, parcel, author));
            } else {
                log.warn("uploading file without ParcelID");
            }
            message = "Uploaded successfully: " + uploadedFileName;
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        } catch (ResourceNotFoundException e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + "! " + e.getMessage();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        } catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }
    }

    @GetMapping
    public ResponseEntity<List<Files>> getListFiles(@RequestParam(required = true) Integer parcelId) {
        log.info("Getting List of Files for parcel with ID " + parcelId);
        List<Files> res = filesRepo.findByParcelId(parcelId);
        res.forEach(f -> f.setUrl(MvcUriComponentsBuilder
                .fromMethodName(FilesController.class, "getFile", f.getName()).build().toString()));
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

//    @GetMapping(value = "/getFile/{filename}")
//    public @ResponseBody byte[] getFileByName(@PathVariable String filename) throws IOException {
////        InputStream in = getClass()
////                .getResourceAsStream("/com/baeldung/produceimage/image.jpg");
//        return IOUtils.toByteArray(storageService.load(filename).getInputStream());
//    }

    @GetMapping("/getFile")
    @ResponseBody
    public ResponseEntity<Resource> getFile(@RequestParam(required = true) String fileName) {
        Resource file = storageService.load(fileName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
//    @GetMapping("/{filename:.+}")
//    @ResponseBody
//    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
//        Resource file = storageService.load(filename);
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
//    }

    @DeleteMapping("/{filename:.+}")
    @ResponseBody
    @Transactional
    public ResponseEntity<ResponseMessage> deleteFile(@PathVariable String filename) {
        try {
            if (storageService.delete(filename)) {
                Files f = filesRepo.findByName(filename);
                if (f != null)
                    filesRepo.delete(f);
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(filename + " Deleted Successfully"));
            }
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage("File Not Found!"));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(e.getMessage()));
        }

    }
}
