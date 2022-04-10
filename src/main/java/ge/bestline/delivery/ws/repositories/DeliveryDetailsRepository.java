package ge.bestline.delivery.ws.repositories;

import ge.bestline.delivery.ws.entities.DeliveryDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeliveryDetailsRepository extends JpaRepository<DeliveryDetail, Integer> {
    Optional<DeliveryDetail> findByDetailBarCode(String barCode);
}
