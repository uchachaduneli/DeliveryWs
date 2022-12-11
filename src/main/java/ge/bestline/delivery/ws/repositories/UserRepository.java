package ge.bestline.delivery.ws.repositories;

import ge.bestline.delivery.ws.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByRouteId(Integer id);

    @Query(nativeQuery = true, value = "select u.* from user_role join user u on u.id = user_role.user_id where role_name in ?1 group by user_id")
    List<User> findAllByRoleNameIn(Set<String> roles);

    User findByPersonalNumber(String identNumber);
}
