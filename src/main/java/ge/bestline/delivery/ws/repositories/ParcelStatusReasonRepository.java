package ge.bestline.delivery.ws.repositories;

import ge.bestline.delivery.ws.entities.ParcelStatus;
import ge.bestline.delivery.ws.entities.ParcelStatusReason;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParcelStatusReasonRepository extends JpaRepository<ParcelStatusReason, Integer> {
    Iterable<ParcelStatusReason> findByStatus_Id(Integer parcelStatusId);
}
