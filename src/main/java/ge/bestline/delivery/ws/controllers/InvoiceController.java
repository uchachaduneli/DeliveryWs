package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.dao.InvoiceDao;
import ge.bestline.delivery.ws.dao.ParcelDao;
import ge.bestline.delivery.ws.dto.*;
import ge.bestline.delivery.ws.entities.Invoice;
import ge.bestline.delivery.ws.entities.Parcel;
import ge.bestline.delivery.ws.repositories.InvoiceRepository;
import ge.bestline.delivery.ws.repositories.ParcelRepository;
import ge.bestline.delivery.ws.security.jwt.JwtTokenProvider;
import ge.bestline.delivery.ws.services.MailService;
import ge.bestline.delivery.ws.services.PDFService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
@RestController
@RequestMapping(path = "/invoice")
public class InvoiceController {

    private final InvoiceRepository repo;
    private final ParcelRepository parcelRepo;
    private final InvoiceDao dao;
    private final ParcelDao parcelDao;
    private final PDFService pdfService;
    private final MailService mailService;
    private final JwtTokenProvider jwtTokenProvider;

    public InvoiceController(InvoiceRepository repo,
                             ParcelRepository parcelRepo,
                             InvoiceDao dao,
                             ParcelDao parcelDao,
                             PDFService pdfService,
                             MailService mailService,
                             JwtTokenProvider jwtTokenProvider) {
        this.repo = repo;
        this.parcelRepo = parcelRepo;
        this.dao = dao;
        this.parcelDao = parcelDao;
        this.pdfService = pdfService;
        this.mailService = mailService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PutMapping("/pay")
    @Transactional
    public Invoice generateInvoice(@RequestBody InvoiceDTO request, HttpServletRequest req) throws ParseException {
        TokenUser requester = jwtTokenProvider.getRequesterUserData(req);
        log.warn(requester.getName() + " " + requester.getLastName() + " makes payment for invoice with ID " + request.getId()
                + " new amount: " + request.getNewAmount());
        Invoice existing = repo.findById(request.getId()).orElseThrow(() -> new ResourceNotFoundException("Can't find Invoice Using This ID : "));
        if (request.getNewAmount() == null) {
            throw new RuntimeException("New Payed Amount Shouldn't be null");
        }
        existing.setPayedAmount((existing.getPayedAmount() == null ? 0.0 : existing.getPayedAmount()) + request.getNewAmount());
        if (existing.getPayedAmount() > existing.getAmount() || existing.getPayedAmount().equals(existing.getAmount())) {
            existing.setPayStatus(InvoicePaymentStatus.PAYED.getStatus());
        } else {
            existing.setPayStatus(InvoicePaymentStatus.PARTIALLY_PAID.getStatus());
        }

        repo.save(existing);
        return existing;
    }

    @PostMapping
    @Transactional
    public Invoice generateInvoice(@RequestBody InvoiceDTO dtoObj) throws ParseException {
        log.info("Generating New Invoice: " + dtoObj.toString());
        if (StringUtils.isNotBlank(dtoObj.getStrOperationDate())) {
            dtoObj.setOperationDate(InvoiceDTO.convertStrDateToDateObj(dtoObj.getStrOperationDate()));
        }
        Invoice obj = new Invoice(dtoObj);
        Double priceSum = 0.0;
        if (obj.getParcels() != null && !obj.getParcels().isEmpty()) {
            List<Parcel> loadedParcels = parcelRepo.findByIdIn(obj.getParcels().stream().map(Parcel::getId).collect(Collectors.toList()));
            for (Parcel p : loadedParcels) {
                if (p.getTotalPrice() == null) {
                    throw new RuntimeException("Price For Parcel:" + p.getBarCode() + " is not defined, you need to fix this");
                }
                priceSum += p.getTotalPrice();
                p.setInvoiced(true);
            }
            parcelRepo.saveAll(loadedParcels);
        } else {
            throw new ResourceNotFoundException("Parcel List Should Not Be Empty");
        }
        obj.setAmount(priceSum);
        obj.setStatus(InvoiceStatus.CREATED.getStatus());
        obj.setPayStatus(InvoicePaymentStatus.UNPAYED.getStatus());
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

    @PostMapping("/email")
    public ResponseEntity<Invoice> meiltest(@RequestBody InvoiceDTO request, HttpServletRequest req) throws ParseException {
        TokenUser requester = jwtTokenProvider.getRequesterUserData(req);
        log.warn(requester.getName() + " " + requester.getLastName() + " tries to send invoice via Email  invoice# " + request.getId()
                + " email: " + request.getEmailToSent());
        Invoice invoice = repo.findById(request.getId()).orElseThrow(() -> new ResourceNotFoundException("Can't find Invoice Using This ID : "));
        try {
            mailService.sendEmail(request.getEmailToSent(), "Invoice #" + invoice.getId(), "",
                    invoice.getPdf() != null ? Arrays.asList(invoice.getPdf()) : null);
        } catch (Exception e) {
            invoice.setStatus(InvoiceStatus.SENT_FAILED.getStatus());
            repo.save(invoice);
            throw new RuntimeException(e);
        }
        invoice.setStatus(InvoiceStatus.SENT.getStatus());
        repo.save(invoice);
        return ResponseEntity.ok(invoice);
    }

    @GetMapping("/statuses")
    public List<String> getStatuses() {
        return Stream.of(InvoiceStatus.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    @GetMapping("/paymentStatuses")
    public List<String> getPaymentStatuses() {
        return Stream.of(InvoicePaymentStatus.values())
                .map(Enum::name)
                .collect(Collectors.toList());
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
            ParcelDTO srchParams) throws ParseException {

        if (StringUtils.isNotBlank(srchParams.getStrCreatedTime())) {
            srchParams.setCreatedTime(ParcelDTO.convertStrDateToDateObj(srchParams.getStrCreatedTime()));
        }
        if (StringUtils.isNotBlank(srchParams.getStrCreatedTimeTo())) {
            srchParams.setCreatedTimeTo(ParcelDTO.convertStrDateToDateObj(srchParams.getStrCreatedTimeTo()));
        }
        srchParams.setPaymentType(1);// always return only invoices
        srchParams.setInvoiced(false);// always return not already invoiced parcels
        return new ResponseEntity<>(parcelDao.findAll(page, rowCount, srchParams, true), HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public Invoice getById(@PathVariable Integer id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID"));
    }
}
