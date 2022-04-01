package ge.bestline.delivery.ws.repositories;

import ge.bestline.delivery.ws.entities.DeliveryDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryDetailRepository extends JpaRepository<DeliveryDetail, Integer> {
}
