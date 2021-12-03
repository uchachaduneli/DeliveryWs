package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.dto.MessageDTO;
import ge.bestline.delivery.ws.entities.Message;
import ge.bestline.delivery.ws.entities.MessageCC;
import ge.bestline.delivery.ws.entities.Parcel;
import ge.bestline.delivery.ws.repositories.MessagesCcRepository;
import ge.bestline.delivery.ws.repositories.MessagesRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping(path = "/messages")
public class MessageController {

    private final MessagesRepository repo;
    private final MessagesCcRepository ccRepo;

    public MessageController(MessagesRepository repo, MessagesCcRepository ccRepo) {
        this.repo = repo;
        this.ccRepo = ccRepo;
    }

    @PostMapping
    @Transactional
    public Message addNew(@RequestBody MessageDTO obj) {
        log.info("Adding New Message: " + obj.toString());
        Message msg = new Message(obj.getSubject(), obj.getComment(), obj.getTo());
        msg = repo.save(msg);
        for (MessageCC p : obj.getCc()) {
            ccRepo.save(p);
        }
        return msg;
    }

    @GetMapping(path = "/cc/{id}")
    public ResponseEntity<List<MessageCC>> getCCByMessageId(@PathVariable Integer id) {
        log.info("Getting CC By Message ID: " + id);
        return ResponseEntity.ok(ccRepo.findByMessageId(id));
    }

    @PutMapping
    @Transactional
    public ResponseEntity<Message> updateById(@RequestBody Message request) {
        log.info("Updating Message");
        Message existing = repo.findById(request.getId()).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : " + request.getId()));
        log.info("Old Values: " + existing.toString() + "    New Values: " + request.toString());
        Message updatedObj = repo.save(existing);
        return ResponseEntity.ok(updatedObj);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Map<String, Boolean>> delete(@PathVariable Integer id) {
        Message existing = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : " + id));
        log.info("Deleting Parcel: " + existing.toString());
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
            Parcel searchParams) {
        Map<String, Object> resp = new HashMap<>();
        Pageable paging = PageRequest.of(page, rowCount, Sort.by("id").descending());
        Page<Message> pageAuths = null;
        pageAuths = repo.findAll(paging);
        resp.put("items", pageAuths.getContent());
        resp.put("total_count", pageAuths.getTotalElements());
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<List<Message>> getMessagesByParcelId(@PathVariable Integer id) {
        log.info("Getting getMessages By Parcel ID: " + id);
        return ResponseEntity.ok(repo.findByParcelIdOrderByIdDesc(id));
    }

    @GetMapping(path = "/messageCC/{id}")
    public ResponseEntity<List<MessageCC>> getCCesByMessageId(@PathVariable Integer id) {
        log.info("Getting MessageCC By Message ID: " + id);
        return ResponseEntity.ok(ccRepo.findByMessageId(id));
    }

//    @PostMapping("/messageCC")
//    @Transactional
//    public ResponseEntity<List<MessageCC>> addNewMessageCC(@RequestBody List<MessageCC> list) {
//        log.info("Adding New MessageCCes: " + list.toString());
//        List<MessageCC> res = new ArrayList<>();
//        for (MessageCC p : list) {
//            res.add(ccRepo.save(p));
//        }
//        return ResponseEntity.ok(res);
//    }
}
