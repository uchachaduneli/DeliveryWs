package ge.bestline.delivery.ws.repositories;

import ge.bestline.delivery.ws.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByName(String admin);
}
