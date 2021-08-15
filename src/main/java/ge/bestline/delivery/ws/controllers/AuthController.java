package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.dao.UserDao;
import ge.bestline.delivery.ws.dto.JwtRequest;
import ge.bestline.delivery.ws.dto.Token;
import ge.bestline.delivery.ws.dto.TokenUser;
import ge.bestline.delivery.ws.entities.User;
import ge.bestline.delivery.ws.security.jwt.JwtTokenProvider;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Log4j2
@RestController
@RequestMapping(value = "/auth")
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDao userDao;

    public AuthController(JwtTokenProvider jwtTokenProvider, UserDao userDao) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDao = userDao;
    }

    @PostMapping("/login")
    public ResponseEntity<?> token(@RequestBody JwtRequest credentials) {
        log.info("Authorization Started for User " + credentials.getUsername());
        try {
            User user = userDao.findByUserNameAndPassword(credentials.getUsername(), credentials.getPassword());
            if (user != null) {
                TokenUser tokenUser = new TokenUser(user);
                return ResponseEntity.ok(new Token(LocalDateTime.now(), tokenUser, this.jwtTokenProvider.createToken(tokenUser)));
            } else {
                log.error("Authorization Failed, User Not Found With Defined Credentials");
                return new ResponseEntity<>((HttpStatus.UNAUTHORIZED));
            }
        } catch (Exception e) {
            log.error("Authorization Failed,  User Not Found for user " + credentials.getUsername());
            return new ResponseEntity<>((HttpStatus.UNAUTHORIZED));
        }
    }
}
