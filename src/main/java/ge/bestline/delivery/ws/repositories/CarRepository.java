package ge.bestline.delivery.ws.repositories;

import ge.bestline.delivery.ws.entities.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Integer> {
}
