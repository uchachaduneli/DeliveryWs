package ge.bestline.delivery.ws;

import ge.bestline.delivery.ws.entities.City;
import ge.bestline.delivery.ws.entities.Role;
import ge.bestline.delivery.ws.entities.UserStatus;
import ge.bestline.delivery.ws.entities.Zone;
import ge.bestline.delivery.ws.repositories.CityRepository;
import ge.bestline.delivery.ws.repositories.RoleRepository;
import ge.bestline.delivery.ws.repositories.UserStatusRepository;
import ge.bestline.delivery.ws.repositories.ZoneRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;

import static org.springframework.web.servlet.function.RequestPredicates.GET;
import static org.springframework.web.servlet.function.RouterFunctions.route;

@SpringBootApplication
public class SpbootApplication {

    public static void main(String[] args) {

        SpringApplication.run(SpbootApplication.class, args);
    }

    @Bean
    RouterFunction<ServerResponse> routerFunction() {
        return route(GET("/swagger"), req ->
                ServerResponse.temporaryRedirect(URI.create("swagger-ui.html")).build());
    }

    // insert roles
    @Bean
    CommandLineRunner init(RoleRepository roleRepository,
                           ZoneRepository zoneRepository,
                           CityRepository cityRepository,
                           UserStatusRepository userStatusRepository) {
        return args -> {
            roleRepository.save(new Role(1, "ADMIN", "ადმინი"));
            roleRepository.save(new Role(2, "OPERATOR", "ოპერატორი"));
            roleRepository.save(new Role(3, "MANAGER", "მენეჯერი"));
            roleRepository.save(new Role(4, "COURIER", "კურიერი"));
            roleRepository.save(new Role(5, "DRIVER", "მძღოლი"));

            zoneRepository.save(new Zone(1, "ზონა 1"));
            zoneRepository.save(new Zone(2, "ზონა 2"));
            zoneRepository.save(new Zone(3, "ზონა 3"));
            zoneRepository.save(new Zone(4, "ზონა 4"));
            zoneRepository.save(new Zone(5, "ზონა 5"));

            cityRepository.save(new City(1, "თბილისი", null));
            cityRepository.save(new City(2, "ბათუმი", null));
            cityRepository.save(new City(3, "ხაშური", null));
            cityRepository.save(new City(4, "ქუთაისი", null));
            cityRepository.save(new City(5, "ზუგდიდი", null));

            userStatusRepository.save(new UserStatus(1, "აქტიური"));
            userStatusRepository.save(new UserStatus(2, "პასიური"));


        };
    }
}
