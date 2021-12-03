package ge.bestline.delivery.ws.repositories;

import ge.bestline.delivery.ws.entities.TariffDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TariffDetailsRepository extends JpaRepository<TariffDetail, Integer> {
    Iterable<TariffDetail> findByTariff_Id(Integer id);

    List<TariffDetail> findByTariffIdAndZoneIdAndWeightGreaterThanOrderByWeightAsc(Integer tariffId, Integer zoneId, Double weight);

    List<TariffDetail> findByTariffIdAndZoneIdAndWeight(Integer tariffId, Integer zoneId, Double weight);
}
