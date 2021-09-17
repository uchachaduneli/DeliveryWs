package ge.bestline.delivery.ws.repositories;

import ge.bestline.delivery.ws.entities.ParcelStatusReason;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParcelStatusReasonRepository extends JpaRepository<ParcelStatusReason, Integer> {
    List<ParcelStatusReason> findByStatus_Id(Integer parcelStatusId);
}
