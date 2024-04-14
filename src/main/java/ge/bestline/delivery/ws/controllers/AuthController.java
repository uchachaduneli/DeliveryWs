package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.dto.JwtRequest;
import ge.bestline.delivery.ws.dto.Token;
import ge.bestline.delivery.ws.dto.TokenUser;
import ge.bestline.delivery.ws.repositories.UserRepository;
import ge.bestline.delivery.ws.security.jwt.JwtTokenProvider;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping(value = "/auth")
public class AuthController {

    @Autowired
    JwtTokenProvider jwtService;

    @Autowired
    UserRepository userRepo;

    @Autowired
    AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<?> token(@RequestBody JwtRequest request) {
        log.info("Authorization Started for User " + request.getUsername());
        try {
            Authentication authentication = authenticationManager
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.getUsername(),
                                    request.getPassword()
                            )
                    );
            if (authentication.isAuthenticated()) {
                TokenUser user = new TokenUser(userRepo.findByUserName(request.getUsername()));
                return ResponseEntity.ok(new Token(jwtService.GenerateToken(user), user));
            } else {
                log.error("Authorization Failed, User Not Found With Requested Credentials");
                throw new UsernameNotFoundException("invalid user request..!!");
            }
        } catch (Exception e) {
            log.error("Authorization Failed for user: " + request.getUsername(), e);
            return new ResponseEntity<>((HttpStatus.UNAUTHORIZED));
        }
    }
}
