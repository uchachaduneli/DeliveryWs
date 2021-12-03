package ge.bestline.delivery.ws.repositories;

import ge.bestline.delivery.ws.entities.MessageCC;
import ge.bestline.delivery.ws.entities.ParcelStatusReason;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessagesCcRepository extends JpaRepository<MessageCC, Integer> {
    List<MessageCC> findByMessageId(Integer messageId);
}
