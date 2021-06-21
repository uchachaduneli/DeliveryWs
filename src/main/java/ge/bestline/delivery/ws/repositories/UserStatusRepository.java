package ge.bestline.delivery.ws.repositories;

import ge.bestline.delivery.ws.entities.City;
import ge.bestline.delivery.ws.entities.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStatusRepository extends JpaRepository<UserStatus, Integer> {
}
