package ge.bestline.delivery.ws.repositories;

import ge.bestline.delivery.ws.entities.Route;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RouteRepository extends JpaRepository<Route, Integer> {
    Iterable<Route> findByCityIdAndDeleted(Integer id, Integer deleted);

    Page<Route> findByCityIdAndDeleted(Integer id, Pageable paging, Integer deleted);

    Page<Route> findByNameContainingIgnoreCaseAndDeleted(String name, Pageable paging, Integer deleted);

    Optional<Route> findByIdAndDeleted(Integer id, int i);
}
