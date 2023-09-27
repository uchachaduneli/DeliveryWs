package ge.bestline.delivery.ws.repositories;

import ge.bestline.delivery.ws.entities.ParcelStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ParcelStatusHistoryRepo extends JpaRepository<ParcelStatusHistory, Integer> {
    List<ParcelStatusHistory> findByParcelId(Integer id);

    List<ParcelStatusHistory> findByParcelIdOrderByStatusDateTimeAsc(Integer id);

    @Query(nativeQuery = true, value = "select * from parcel_status_history where parcel_id=?1 and code=?2 order by id desc limit 1")
    Optional<ParcelStatusHistory> findTheLastWithParcelIdAndStatusCode(Integer id, String code);
}
