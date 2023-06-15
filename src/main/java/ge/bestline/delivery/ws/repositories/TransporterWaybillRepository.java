package ge.bestline.delivery.ws.repositories;

import ge.bestline.delivery.ws.entities.WayBill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Map;
import java.util.Optional;

public interface TransporterWaybillRepository extends JpaRepository<WayBill, Integer> {
    @Query(nativeQuery = true, value = "SELECT TRIM(REGEXP_SUBSTR(w.waybill_comment,'[0-9]+')) AS parsedWaybillComment, w.id FROM way_bill w " +
            "WHERE LENGTH(TRIM(REGEXP_SUBSTR(w.waybill_comment,'[0-9]+')))=8 AND DATE(w.rs_create_date) = CURDATE()")
    Map<String, Integer> getBarCodesFromCurrentDayWaybillsComment();

    @Query(nativeQuery = true, value = "SELECT w.* FROM way_bill w WHERE LENGTH(TRIM(REGEXP_SUBSTR(w.waybill_comment,'[0-9]+')))=8 " +
            "AND TRIM(REGEXP_SUBSTR(w.waybill_comment,'[0-9]+')) = ?")
    Optional<WayBill> findByBarCodeInComment(String parcelBarCode);
}
