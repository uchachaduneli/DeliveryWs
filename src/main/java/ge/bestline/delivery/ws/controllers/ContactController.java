package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.dao.ContactDao;
import ge.bestline.delivery.ws.dto.TokenUser;
import ge.bestline.delivery.ws.entities.Contact;
import ge.bestline.delivery.ws.entities.User;
import ge.bestline.delivery.ws.repositories.ContactRepository;
import ge.bestline.delivery.ws.repositories.UserRepository;
import ge.bestline.delivery.ws.security.jwt.JwtTokenProvider;
import ge.bestline.delivery.ws.util.ExcelHelper;
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

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping(path = "/contact")
public class ContactController {

    private final ContactRepository repo;
    private final ContactDao dao;
    private final UserRepository userRepository;
    private final ExcelHelper excelHelper;
    private final JwtTokenProvider jwtTokenProvider;

    public ContactController(ContactRepository repo, ContactDao dao,
                             UserRepository userRepository, ExcelHelper excelHelper,
                             JwtTokenProvider jwtTokenProvider) {
        this.repo = repo;
        this.dao = dao;
        this.userRepository = userRepository;
        this.excelHelper = excelHelper;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        return new ResponseEntity<>("თქვენ უკვე გყავთ მითითებული საიდენტიფიკაციო ნომრით კონტაქტი", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/excel")
    public ResponseEntity<Resource> downloadExcell(Contact searchParams) {
        log.info("Excel Generation & Download Started ");
        try {
            InputStreamResource file = new InputStreamResource(excelHelper.contactsToExcelFile(repo.findAll()));
            log.info("Excel Generation Finished, Returning The File");
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=contacts.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(file);
        } catch (Exception ex) {
            log.error("Error Occurred During Excel Generation", ex);
            return new ResponseEntity<Resource>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    @Transactional
    public Contact addNew(@RequestBody Contact obj) {
        log.info("Adding New Contact: " + obj.toString());
        if (obj.getIdentNumber() != null && repo.findFirstByIdentNumber(obj.getIdentNumber()) != null) {
            throw new ConstraintViolationException("Already Exists", new SQLException(), "identNumber");
        }
        return repo.save(obj);
    }

    @PutMapping
    @Transactional
    public ResponseEntity<Contact> updateById(@RequestBody Contact request) {
        log.info("Updating Contact");
        Contact existing = repo.findById(request.getId()).orElseThrow(() ->
                new ResourceNotFoundException("Can't find Record Using This ID : " + request.getId()));
        Contact foundedByIdent = repo.findFirstByIdentNumber(request.getIdentNumber());
        if (foundedByIdent != null && existing.getId() != foundedByIdent.getId()) {
            throw new ConstraintViolationException("Already Exists", new SQLException(), "identNumber");
        }
        log.info("Old Values: " + existing.toString() + "    New Values: " + request.toString());
        existing.setName(request.getName());
        existing.setEmail(request.getEmail());
        existing.setDeReGe(request.getDeReGe());
        existing.setIdentNumber(request.getIdentNumber());
        existing.setStatus(request.getStatus());
        existing.setType(request.getType());
        existing.setHasContract(request.getHasContract());
        existing.setTariff(request.getTariff());
        existing.setUser(userRepository.findById(request.getUser().getId()).orElseThrow(() ->
                new ResourceNotFoundException("Can't find User Using This ID : " + request.getUser().getId())));
        Contact updatedObj = repo.save(existing);
        return ResponseEntity.ok(updatedObj);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Map<String, Boolean>> delete(@PathVariable Integer id) {
        Contact existing = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : " + id));
        log.info("Deleting Contact: " + existing.toString());
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
            Contact searchParams,
            HttpServletRequest req) {
        TokenUser requester = jwtTokenProvider.getRequesterUserData(req);
        if (requester.isFromGlobalSite()) {
            searchParams.setUser(new User(requester.getId()));
        }
        return new ResponseEntity<>(dao.findAll(page, rowCount, searchParams), HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public Contact getById(@PathVariable Integer id) {
        log.info("Getting Contact With ID: " + id);
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID"));
    }

    @GetMapping(path = "/byIdentNum/{identNum}")
    public Contact getByIdentNumber(@PathVariable String identNum) {
        log.info("Getting Contact With identNum: " + identNum);
        Contact c = repo.findFirstByIdentNumber(identNum);
        if (c != null)
            return c;
        else throw new ResourceNotFoundException("Can't find Record Using This identNumber: " + identNum);
    }

}
