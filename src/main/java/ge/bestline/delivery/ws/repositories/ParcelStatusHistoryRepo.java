package ge.bestline.delivery.ws.repositories;

import ge.bestline.delivery.ws.entities.ParcelStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ParcelStatusHistoryRepo extends JpaRepository<ParcelStatusHistory, Integer> {
    List<ParcelStatusHistory> findByParcelId(Integer id);

    List<ParcelStatusHistory> findByParcelIdOrderByStatusDateTimeAsc(Integer id);

    @Query(nativeQuery = true, value = "select h.* from parcel_status_history h where h.parcel_id=? and h.code=? order by id desc limit 1")
    Optional<ParcelStatusHistory> findTheLastWithParcelIdAndStatusCode(Integer id, String code);
}
