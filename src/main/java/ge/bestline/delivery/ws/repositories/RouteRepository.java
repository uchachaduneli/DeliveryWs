package ge.bestline.delivery.ws.repositories;

import ge.bestline.delivery.ws.entities.Route;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RouteRepository extends JpaRepository<Route, Integer> {
    Iterable<Route> findByCity_Id(Integer id);
}
