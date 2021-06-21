package ge.bestline.delivery.ws.repositories;

import ge.bestline.delivery.ws.entities.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteRepository extends JpaRepository<City, Integer> {
}
