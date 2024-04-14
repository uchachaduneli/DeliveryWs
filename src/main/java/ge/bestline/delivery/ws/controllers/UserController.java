package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.dao.UserDao;
import ge.bestline.delivery.ws.dto.CourierCheckInOutDTO;
import ge.bestline.delivery.ws.dto.TokenUser;
import ge.bestline.delivery.ws.entities.User;
import ge.bestline.delivery.ws.entities.UserStatus;
import ge.bestline.delivery.ws.repositories.ContactRepository;
import ge.bestline.delivery.ws.repositories.ParcelRepository;
import ge.bestline.delivery.ws.repositories.UserRepository;
import ge.bestline.delivery.ws.repositories.UserStatusRepository;
import ge.bestline.delivery.ws.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Log4j2
@RestController
@RequestMapping(path = "/user")
public class UserController {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserDao userDao;
    private final JwtTokenProvider jwtTokenProvider;
    private final ContactRepository contactRepo;
    private final ParcelRepository parcelRepo;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository,
                          UserStatusRepository userStatusRepository,
                          UserDao userDao,
                          JwtTokenProvider jwtTokenProvider,
                          ContactRepository contactRepo,
                          ParcelRepository parcelRepo,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userStatusRepository = userStatusRepository;
        this.userDao = userDao;
        this.jwtTokenProvider = jwtTokenProvider;
        this.contactRepo = contactRepo;
        this.parcelRepo = parcelRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        return new ResponseEntity<>("მითითებული პირადი ნომრით მომხმარებელი უკვე არსებობს", HttpStatus.BAD_REQUEST);
    }

    @PostMapping
    @Transactional
    public User addNewUser(@RequestBody User user,
                           HttpServletRequest req) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

//    @PostMapping
//    @Transactional
//    public User addNewUser(@RequestBody User user,
//                           HttpServletRequest req) {
//        log.info("Adding New User: " + user.toString());
//        TokenUser requester = jwtTokenProvider.getRequesterUserData(req);
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        //when customer are adding sub customer user
//        if (requester.getRole().size() == 1 && requester.getRole().contains(UserRoles.CUSTOMER.getValue())) {
//            user.setParentUserId(requester.getId());
//            Set<Role> roles = new HashSet<>();
//            roles.add(new Role(UserRoles.CUSTOMER.getValue()));
//            user.setRole(roles);
//        } else         // when office adds user for customer company
//            if (user.getRole().size() == 1 &&
//                    new ArrayList<>(user.getRole()).get(0).getName().equals(UserRoles.CUSTOMER.getValue())) {
//                User res = userRepository.save(user);
//                Contact contact = new Contact(user.getName() + " " + user.getLastName(),
//                        1, 1, 2, 1,
//                        user.getPersonalNumber(),
//                        res, new Tariff(1));
//                contactRepo.save(contact);
//                // if customer have sent parcels before office create for him account
//                // then update old parcels author id to bind this user now
//                List<Parcel> previouslySentParcels = parcelRepo.findBySenderIdentNumberAndDeleted(user.getPersonalNumber(), 2);
//                if (previouslySentParcels != null && !previouslySentParcels.isEmpty()) {
//                    for (Parcel p : previouslySentParcels) {
//                        p.setAuthor(res);
//                    }
//                    parcelRepo.saveAll(previouslySentParcels);
//                }
//            }
//
//        return userRepository.save(user);
//    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(@RequestParam(required = false, defaultValue = "0") int page,
                                                      @RequestParam(required = false, defaultValue = "10") int rowCount,
                                                      User searchParams,
                                                      HttpServletRequest req) {
        log.info("Getting Users with params: " + searchParams);
        TokenUser requester = jwtTokenProvider.getRequesterUserData(req);
        if (requester.isFromGlobalSite()) {// customer sees sub customers only
            searchParams.setParentUserId(requester.getId());
        }
        return new ResponseEntity<>(userDao.findAll(page, rowCount, searchParams), HttpStatus.OK);
    }

    @GetMapping("/CoutiersInOut")
    public ResponseEntity<Map<String, Object>> getCoutiersInOut(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int rowCount,
            CourierCheckInOutDTO searchParams, HttpServletRequest req) {
        log.info("Getting Coutiers In/Out with params: " + searchParams);
        return new ResponseEntity<>(userDao.getCoutiersInOut(page, rowCount, searchParams), HttpStatus.OK);
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
    @Transactional
    public ResponseEntity<User> updateById(@RequestBody User request) {
        log.info("Updating User");
        User user = userRepository.findById(request.getId()).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID : " + request.getId()));
        log.info("Old Values: " + user.toString() + "    New Values: " + request.toString());
        user.setName(request.getName());
        user.setUserName(request.getUserName());

        userDao.removeUserExistingRoles(user.getId());
        user.setRole(request.getRole());

        user.setLastName(request.getLastName());
        user.setCity(request.getCity());
        user.setRoute(request.getRoute());
        user.setWarehouse(request.getWarehouse());
        user.setPersonalNumber(request.getPersonalNumber());
        user.setPhone(request.getPhone());
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

    @GetMapping("/byRoles")
    public ResponseEntity<List<User>> getUsersHavingRoles(@RequestParam Set<String> roles) {
        return new ResponseEntity<>(userRepository.findAllByRoleNameIn(roles), HttpStatus.OK);
    }
}
