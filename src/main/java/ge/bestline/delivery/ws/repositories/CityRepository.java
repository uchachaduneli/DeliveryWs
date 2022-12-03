package ge.bestline.delivery.ws.repositories;

import ge.bestline.delivery.ws.entities.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Integer> {
    Optional<City> findByIdAndDeleted(Integer cityOneId, int i);
}
