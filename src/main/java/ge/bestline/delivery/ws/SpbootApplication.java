package ge.bestline.delivery.ws;

import ge.bestline.delivery.ws.entities.*;
import ge.bestline.delivery.ws.repositories.*;
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
                           UserStatusRepository userStatusRepository,
                           CarRepository carRepository,
                           ParcelStatusRepository parcelStatusRepository,
                           WarehouseRepository warehouseRepository,
                           ServicesRepository servicesRepository) {
        return args -> {
            roleRepository.save(new Role(1, "ADMIN", "ადმინი"));
            roleRepository.save(new Role(2, "OPERATOR", "ოპერატორი"));
            roleRepository.save(new Role(3, "MANAGER", "მენეჯერი"));
            roleRepository.save(new Role(4, "COURIER", "კურიერი"));
            roleRepository.save(new Role(5, "DRIVER", "მძღოლი"));

            Zone z1 = new Zone(1, "ზონა 1");
            Zone z2 = new Zone(2, "ზონა 2");
            Zone z3 = new Zone(3, "ზონა 3");

            zoneRepository.save(z1);
            zoneRepository.save(z2);
            zoneRepository.save(z3);

            City c1 = new City(1, "თბილისი", z1);
            City c2 = new City(2, "ქუთაისი", z2);
            City c3 = new City(3, "ზუგდიდი", z3);

            cityRepository.save(c1);
            cityRepository.save(c2);
            cityRepository.save(c3);

            warehouseRepository.save(new Warehouse(1, "თბილისის საწყობი", c1));
            warehouseRepository.save(new Warehouse(2, "ქუთაისის საწყობი", c2));
            warehouseRepository.save(new Warehouse(3, "ზუგდიდის საწყობი", c3));

            userStatusRepository.save(new UserStatus(1, "აქტიური"));
            userStatusRepository.save(new UserStatus(2, "პასიური"));

            carRepository.save(new Car(1, "Ford Transit", "AA-123-BB"));
            carRepository.save(new Car(2, "Renault", "CC-321-BB"));

            parcelStatusRepository.save(new ParcelStatus(1, "გზავნილი აღებულია", "PU"));
            parcelStatusRepository.save(new ParcelStatus(2, "შემოწმება უსაფრთხოებაზე", "SI"));
            parcelStatusRepository.save(new ParcelStatus(3, "შემოსვლა სადგურში", "AR"));

            servicesRepository.save(new Services(1, "12-მდე"));
            servicesRepository.save(new Services(2, "სტანდარტული"));
            servicesRepository.save(new Services(3, "შაბათის სერვისი"));
            servicesRepository.save(new Services(4, "პირდაპირი ჩაბარება"));
            servicesRepository.save(new Services(4, "იმავე დღის ჩაბარება"));
        };
    }
}
