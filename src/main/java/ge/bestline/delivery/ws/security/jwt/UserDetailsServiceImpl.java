package ge.bestline.delivery.ws.security.jwt;

import ge.bestline.delivery.ws.dao.UserDao;
import ge.bestline.delivery.ws.entities.User;
import ge.bestline.delivery.ws.repositories.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Component
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepo;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Entering in loadUserByUsername Method...");
        User user = userRepo.findByUserName(username);
        if (user == null) {
            log.error("Username not found: " + username);
            throw new UsernameNotFoundException("could not found user..!!");
        }
        log.info("User Authenticated Successfully..!!!");
        return new CustomUserDetails(user);
    }
}
