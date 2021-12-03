package ge.bestline.delivery.ws.repositories;

import ge.bestline.delivery.ws.entities.ParcelStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParselStatusHistoryRepo extends JpaRepository<ParcelStatusHistory, Integer> {
    List<ParcelStatusHistory> findByParcelId(Integer id);
}
