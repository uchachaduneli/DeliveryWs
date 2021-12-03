package ge.bestline.delivery.ws.repositories;

import ge.bestline.delivery.ws.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessagesRepository extends JpaRepository<Message, Integer> {
    List<Message> findByParcelIdOrderByIdDesc(Integer id);
}
