package ge.bestline.delivery.ws.repositories;

import ge.bestline.delivery.ws.entities.City;
import ge.bestline.delivery.ws.entities.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Integer> {
}
