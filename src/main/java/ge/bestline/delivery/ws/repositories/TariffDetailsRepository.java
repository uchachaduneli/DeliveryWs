package ge.bestline.delivery.ws.repositories;

import ge.bestline.delivery.ws.entities.TariffDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TariffDetailsRepository extends JpaRepository<TariffDetail, Integer> {
    Iterable<TariffDetail> findByTariff_Id(Integer id);
}
