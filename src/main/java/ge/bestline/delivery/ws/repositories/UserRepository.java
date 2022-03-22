package ge.bestline.delivery.ws.repositories;

import ge.bestline.delivery.ws.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByRouteId(Integer id);
}
