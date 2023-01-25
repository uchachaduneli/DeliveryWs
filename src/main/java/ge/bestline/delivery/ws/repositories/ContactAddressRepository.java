package ge.bestline.delivery.ws.repositories;

import ge.bestline.delivery.ws.entities.ContactAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactAddressRepository extends JpaRepository<ContactAddress, Integer> {

    List<ContactAddress> findByContact_Id(Integer contactId);

    ContactAddress findByIsPayAddress(int i);

    ContactAddress findFirstByContact_Id(Integer id);

}
