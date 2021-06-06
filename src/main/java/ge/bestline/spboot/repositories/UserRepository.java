package ge.bestline.spboot.repositories;

import ge.bestline.spboot.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
