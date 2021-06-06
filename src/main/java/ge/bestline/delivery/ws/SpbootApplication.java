package ge.bestline.delivery.ws;

import ge.bestline.delivery.ws.entities.Role;
import ge.bestline.delivery.ws.repositories.RoleRepository;
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

    // insert roles by default
    @Bean
    CommandLineRunner init(RoleRepository roleRepository) {
        return args -> {
            roleRepository.save(new Role(1, "Administrator"));
            roleRepository.save(new Role(2, "Operator"));
            roleRepository.save(new Role(3, "Manager"));
            roleRepository.save(new Role(4, "Courier"));
        };
    }
}
