package ge.bestline.delivery.ws.repositories;

import ge.bestline.delivery.ws.entities.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
}
