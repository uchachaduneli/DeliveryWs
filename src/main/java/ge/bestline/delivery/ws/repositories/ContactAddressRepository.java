package ge.bestline.delivery.ws.repositories;

import ge.bestline.delivery.ws.entities.ContactAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactAddressRepository extends JpaRepository<ContactAddress, Integer> {

    Iterable<ContactAddress> findByContact(Integer id);

    Iterable<ContactAddress> findByContact_Id(Integer contactId);
}
