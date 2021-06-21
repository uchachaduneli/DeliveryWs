package ge.bestline.delivery.ws.repositories;

import ge.bestline.delivery.ws.entities.ParcelStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParcelStatusRepository extends JpaRepository<ParcelStatus, Integer> {
}
