package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.dao.UserDao;
import ge.bestline.delivery.ws.entities.User;
import ge.bestline.delivery.ws.entities.UserStatus;
import ge.bestline.delivery.ws.repositories.UserRepository;
import ge.bestline.delivery.ws.repositories.UserStatusRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping(path = "/user")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserDao userDao;

    public UserController(UserRepository userRepository, UserStatusRepository userStatusRepository, UserDao userDao) {
        this.userRepository = userRepository;
        this.userStatusRepository = userStatusRepository;
        this.userDao = userDao;
    }

    @PostMapping
    public User addNewUser(@RequestBody User user) {
        log.info("Adding New User: " + user.toString());
        return userRepository.save(user);
    }
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(User searchParams) {
        log.info("Getting Contact Addresses with params: " + searchParams);
        return new ResponseEntity<>(userDao.findAll(searchParams), HttpStatus.OK);
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

    @PostMapping(path = "/{id}")
    public ResponseEntity<User> updateById(@PathVariable Integer id, @RequestBody User request) {
        log.info("Updating User");
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : " + id));
        log.info("Old Values: " + user.toString() + "    New Values: " + request.toString());
        user.setName(request.getName());
//        user.setEmail(request.getEmail());
        user.setRole(request.getRole());

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
