package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.dao.InvoiceDao;
import ge.bestline.delivery.ws.dto.InvoiceDTO;
import ge.bestline.delivery.ws.entities.Invoice;
import ge.bestline.delivery.ws.entities.Parcel;
import ge.bestline.delivery.ws.repositories.InvoiceRepository;
import ge.bestline.delivery.ws.repositories.ParcelRepository;
import ge.bestline.delivery.ws.services.MailService;
import ge.bestline.delivery.ws.services.PDFService;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.naming.ConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@RestController
@RequestMapping(path = "/invoice")
public class InvoiceController {

    private final InvoiceRepository repo;
    private final ParcelRepository parcelRepo;
    private final InvoiceDao dao;
    private final PDFService pdfService;
    private final MailService mailService;

    public InvoiceController(InvoiceRepository repo,
                             ParcelRepository parcelRepo,
                             InvoiceDao dao,
                             PDFService pdfService, MailService mailService) {
        this.repo = repo;
        this.parcelRepo = parcelRepo;
        this.dao = dao;
        this.pdfService = pdfService;
        this.mailService = mailService;
    }

    @PostMapping
    @Transactional
    public Invoice addNew(@RequestBody Invoice obj) {
        log.info("Adding New Invoice: " + obj.toString());
        if (obj.getParcels() != null && !obj.getParcels().isEmpty()) {
            List<Parcel> loadedParcels = parcelRepo.findByIdIn(obj.getParcels().stream().map(Parcel::getId).collect(Collectors.toList()));
            for (Parcel p : loadedParcels) {
                p.setInvoiced(true);
            }
            parcelRepo.saveAll(loadedParcels);
        } else {
            throw new ResourceNotFoundException("Parcel List Should Not Be Empty");
        }
        Invoice res = repo.save(obj);
        try {
            res.setPdf(pdfService.generateInvoice(res));
            repo.save(res);
        } catch (Exception e) {
            repo.delete(res);
            throw new RuntimeException("Can't generate pdf ", e);
        }
        return res;
    }

    @GetMapping("/test")
    public String genPdf() {
        Invoice res = repo.findById(1).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : "));
        try {
            return pdfService.generateInvoice(res);
        } catch (Exception e) {
            throw new RuntimeException("Can't generate pdf ", e);
        }
    }

    @GetMapping("/meiltest")
    public String meiltest() throws MessagingException, ConfigurationException, IOException {
        mailService.sendEmail("uchachaduneli@gmail.com", "რამე საბჯექთი", "სომე ბოდი some body", null);
        return "ok";
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Map<String, Boolean>> delete(@PathVariable Integer id) {
        Invoice existing = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : " + id));
        log.info("Deleting Invoice: " + existing.toString());
        for (Parcel p : existing.getParcels()) {
            p.setInvoiced(false);
        }
        parcelRepo.saveAll(existing.getParcels());

        repo.delete(existing);
        Map<String, Boolean> resp = new HashMap<>();
        resp.put("deleted", Boolean.TRUE);
        return ResponseEntity.ok(resp);
    }

    //ukve daregistrirebuli invoisebi
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int rowCount,
            InvoiceDTO searchParams) {
        return new ResponseEntity<>(dao.findAll(page, rowCount, searchParams), HttpStatus.OK);
    }

    // generaciis feijze shesvlisas gamosatani sia
    @GetMapping("/notYetGenerated")
    public ResponseEntity<Map<String, Object>> loadAllForInvoiceGeneration(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int rowCount,
            InvoiceDTO srchObj) {
        return new ResponseEntity<>(dao.loadAllForInvoiceGeneration(page, rowCount, srchObj), HttpStatus.OK);
    }

    // generaciis feijze romelimes archevis mere gamosatani amanatebis sia saidanac amoakleben Tu romelime
    // ar undad am invoishi ro ijdes
    @GetMapping("/payerUnInvoicedParcels")
    public ResponseEntity<Map<String, Object>> getPayerUnInvoicedParcels(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int rowCount,
            @RequestParam(required = true) String identNumber) {
        Map<String, Object> resp = new HashMap<>();
        Pageable paging = PageRequest.of(page, rowCount, Sort.by("id").descending());
        Page<Parcel> pageAuths = parcelRepo.findByPayerIdentNumberAndDeletedAndInvoiced(identNumber, 2, false, paging);
        resp.put("items", pageAuths.getContent());
        resp.put("total_count", pageAuths.getTotalElements());
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public Invoice getById(@PathVariable Integer id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID"));
    }
}
