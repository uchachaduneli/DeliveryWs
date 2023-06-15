package ge.bestline.delivery.ws.repositories;

import ge.bestline.delivery.ws.entities.CourierCheckInOut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ChekInOutRepository extends JpaRepository<CourierCheckInOut, Integer> {

    @Query(nativeQuery = true, value = "select * from courier_check_in_out " +
            "where courier_id = ?1 and is_chek_in=0 order by id desc limit 1")
    Optional<CourierCheckInOut> findCouriersLastCheckoutRecord(Integer courierId);
}
