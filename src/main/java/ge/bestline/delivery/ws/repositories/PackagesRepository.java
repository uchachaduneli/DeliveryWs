package ge.bestline.delivery.ws.repositories;

import ge.bestline.delivery.ws.entities.Car;
import ge.bestline.delivery.ws.entities.Packages;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PackagesRepository extends JpaRepository<Packages, Integer> {
    List<Packages> findByParcelId(Integer id);
}
