package ge.bestline.delivery.ws.repositories;

import ge.bestline.delivery.ws.entities.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseRepository extends JpaRepository<Warehouse, Integer> {
    Iterable<Warehouse> findByCityIdAndDeleted(Integer id, Integer deleted);
}
