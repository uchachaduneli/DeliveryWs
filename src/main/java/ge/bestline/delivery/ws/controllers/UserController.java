package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.dao.UserDao;
import ge.bestline.delivery.ws.entities.User;
import ge.bestline.delivery.ws.entities.UserStatus;
import ge.bestline.delivery.ws.repositories.UserRepository;
import ge.bestline.delivery.ws.repositories.UserStatusRepository;
import lombok.extern.log4j.Log4j2;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping(path = "/user")
public class UserController {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserDao userDao;

    public UserController(UserRepository userRepository, UserStatusRepository userStatusRepository, UserDao userDao) {
        this.userRepository = userRepository;
        this.userStatusRepository = userStatusRepository;
        this.userDao = userDao;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        return new ResponseEntity<>("მითითებული პირადი ნომრით მომხმარებელი უკვე არსებობს", HttpStatus.BAD_REQUEST);
    }

    @PostMapping
    public User addNewUser(@RequestBody User user) {
        log.info("Adding New User: " + user.toString());
        return userRepository.save(user);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(@RequestParam(required = false, defaultValue = "0") int page,
                                                      @RequestParam(required = false, defaultValue = "10") int rowCount,
                                                      User searchParams) {
        log.info("Getting Users with params: " + searchParams);
        return new ResponseEntity<>(userDao.findAll(page, rowCount, searchParams), HttpStatus.OK);
    }

    @GetMapping(path = "/statuses")
    public Iterable<UserStatus> getUserStatuses() {
        return userStatusRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<User> getById(@PathVariable Integer id) {
        log.info("Getting User With ID: " + id);
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : " + id));
        return ResponseEntity.ok(user);
    }

    @PutMapping
    public ResponseEntity<User> updateById( @RequestBody User request) {
        log.info("Updating User");
        User user = userRepository.findById(request.getId()).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : " + request.getId()));
        log.info("Old Values: " + user.toString() + "    New Values: " + request.toString());
        user.setName(request.getName());
        user.setUserName(request.getUserName());
        user.setRole(request.getRole());
        user.setLastName(request.getLastName());
        user.setCity(request.getCity());
        user.setRoute(request.getRoute());
        user.setPersonalNumber(request.getPersonalNumber());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());
        user.setWarehouse(request.getWarehouse());
        User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Boolean>> delete(@PathVariable Integer id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : " + id));
        log.info("Deleting User: " + user.toString());
        user.setDeleted(1);
        userRepository.save(user);
        Map<String, Boolean> resp = new HashMap<>();
        resp.put("deleted", Boolean.TRUE);
        return ResponseEntity.ok(resp);
    }

}
