package ge.bestline.delivery.ws.repositories;

import ge.bestline.delivery.ws.entities.Packages;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PackagesRepository extends JpaRepository<Packages, Integer> {
    List<Packages> findByParcelId(Integer id);
}
