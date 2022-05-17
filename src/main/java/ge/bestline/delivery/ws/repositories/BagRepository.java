package ge.bestline.delivery.ws.repositories;

import ge.bestline.delivery.ws.entities.Bag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BagRepository extends JpaRepository<Bag, Integer> {
    Optional<Bag> findByBarCode(String barCode);

    List<Bag> findByBarCodeIn(List<String> barCodes);
}
