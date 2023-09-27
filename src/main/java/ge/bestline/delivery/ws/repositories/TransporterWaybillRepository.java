package ge.bestline.delivery.ws.repositories;

import ge.bestline.delivery.ws.entities.WayBill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TransporterWaybillRepository extends JpaRepository<WayBill, Integer> {
    @Query(nativeQuery = true, value = "SELECT TRIM(REGEXP_SUBSTR(w.waybill_comment,'[0-9]+')) AS parsedWaybillComment, w.id FROM way_bill w " +
            "WHERE LENGTH(TRIM(REGEXP_SUBSTR(w.waybill_comment,'[0-9]+')))=8 AND DATE(w.begin_date) = CURDATE()")
    List<Object[]> getBarCodesFromCurrentDayWaybillsComment();

    @Query(nativeQuery = true, value = "SELECT w.* FROM way_bill w WHERE LENGTH(TRIM(REGEXP_SUBSTR(w.waybill_comment,'[0-9]+')))=8 " +
            "AND TRIM(REGEXP_SUBSTR(w.waybill_comment,'[0-9]+')) = ? AND w.status_id <> 2")
    Optional<WayBill> findUnClosedByBarCodeInComment(String parcelBarCode);

    Optional<WayBill> findByIdAndStatusIdNot(String waybillId, Integer value);
}
