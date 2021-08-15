package ge.bestline.delivery.ws.repositories;

import ge.bestline.delivery.ws.entities.Route;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteRepository extends JpaRepository<Route, Integer> {
    Iterable<Route> findByCity_Id(Integer id);

    Page<Route> findByCity_Id(Integer id, Pageable paging);

    Page<Route> findByNameContainingIgnoreCase(String name, Pageable paging);
}
