package ge.bestline.delivery.ws.repositories;

import ge.bestline.delivery.ws.entities.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
    Contact findByIdentNumber(String identNumber);
}
