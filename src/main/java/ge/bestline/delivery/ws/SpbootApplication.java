package ge.bestline.delivery.ws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;

import static org.springframework.web.servlet.function.RequestPredicates.GET;
import static org.springframework.web.servlet.function.RouterFunctions.route;

@SpringBootApplication
public class SpbootApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {

        SpringApplication.run(SpbootApplication.class, args);
    }

    @Bean
    RouterFunction<ServerResponse> routerFunction() {
        return route(GET("/swagger"), req ->
                ServerResponse.temporaryRedirect(URI.create("swagger-ui.html")).build());
    }

    // insert test data
//    @Bean
//    CommandLineRunner init(RoleRepository roleRepository,
//                           ZoneRepository zoneRepository,
//                           CityRepository cityRepository,
//                           UserStatusRepository userStatusRepository,
//                           CarRepository carRepository,
//                           ParcelStatusRepository parcelStatusRepository,
//                           WarehouseRepository warehouseRepository,
//                           ServicesRepository servicesRepository,
//                           UserRepository userRepo,
//                           ContactRepository contactRepository,
//                           ContactAddressRepository contactAddressRepository) {
//        return args -> {
//            Set<Role> roles = new HashSet<>();
//            roles.add(new Role("ADMIN"));
//            roles.add(new Role("OPERATOR"));
//            roles.add(new Role("MANAGER"));
//            roles.add(new Role("COURIER"));
//            roles.add(new Role("DRIVER"));
//            roles.forEach(role -> {
//                roleRepository.save(role);
//            });
//
//            Zone z1 = new Zone(1, "ზონა 1");
//            Zone z2 = new Zone(2, "ზონა 2");
//            Zone z3 = new Zone(3, "ზონა 3");
//
//            zoneRepository.save(z1);
//            zoneRepository.save(z2);
//            zoneRepository.save(z3);
//
//            City c1 = new City(1, "თბილისი", z1);
//            City c2 = new City(2, "ქუთაისი", z2);
//            City c3 = new City(3, "ზუგდიდი", z3);
//
//            cityRepository.save(c1);
//            cityRepository.save(c2);
//            cityRepository.save(c3);
//
//            warehouseRepository.save(new Warehouse(1, "თბილისის საწყობი", c1));
//            warehouseRepository.save(new Warehouse(2, "ქუთაისის საწყობი", c2));
//            warehouseRepository.save(new Warehouse(3, "ზუგდიდის საწყობი", c3));
//
//            carRepository.save(new Car(1, "Ford Transit", "AA-123-BB"));
//            carRepository.save(new Car(2, "Renault", "CC-321-BB"));
//
//            parcelStatusRepository.save(new ParcelStatus(1, "გზავნილი აღებულია", "PU"));
//            parcelStatusRepository.save(new ParcelStatus(2, "შემოწმება უსაფრთხოებაზე", "SI"));
//            parcelStatusRepository.save(new ParcelStatus(3, "შემოსვლა სადგურში", "AR"));
//
//            servicesRepository.save(new Services(1, "12-მდე"));
//            servicesRepository.save(new Services(2, "სტანდარტული"));
//            servicesRepository.save(new Services(3, "შაბათის სერვისი"));
//            servicesRepository.save(new Services(4, "პირდაპირი ჩაბარება"));
//            servicesRepository.save(new Services(4, "იმავე დღის ჩაბარება"));
//
//            UserStatus ust1 = new UserStatus(1, "აქტიური");
//            UserStatus ust2 = new UserStatus(2, "პასიური");
//            userStatusRepository.save(ust1);
//            userStatusRepository.save(ust2);
//
//            User u = new User("name1", "lastName1", "phone1", "personalNumber123", c1, roles, ust1);
//            userRepo.save(u);
//
//            Contact contact = new Contact("contact1", "contEmail", 1, 1, 1, "identNumber1", u);
//            Contact contact2 = new Contact("contact2", "contEmail2", 1, 1, 1, "identNumber2", u);
//            contactRepository.save(contact);
//            contactRepository.save(contact2);
//
//            ContactAddress contAddress1 = new ContactAddress(contact, c1, "postCode1", "street1", "Building 1/ flat 10/ floor 3", "contactPerson1", "contactPersonPhone1", "contactPersonEmail1");
//            ContactAddress contAddress2 = new ContactAddress(contact, c1, "postCode2", "street2", "Building 2/ flat 20/ floor 2", "contactPerson2", "contactPersonPhone2", "contactPersonEmail2");
//            ContactAddress contAddress3 = new ContactAddress(contact2, c1, "postCode3", "street3", "Building 3/ flat 30/ floor 4", "contactPerson3", "contactPersonPhone3", "contactPersonEmail3");
//            contactAddressRepository.save(contAddress1);
//            contactAddressRepository.save(contAddress2);
//            contactAddressRepository.save(contAddress3);
//        };
//    }
}
