package ge.bestline.delivery.ws.repositories;

import ge.bestline.delivery.ws.entities.TariffDetail;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TariffDetailsRepository extends JpaRepository<TariffDetail, Integer> {
    Iterable<TariffDetail> findByTariffIdAndDeleted(Integer id, Integer deleted);

    List<TariffDetail> findByDeletedAndService_IdAndTariff_IdAndZone_IdAndWeight(Integer i, Integer serviceId, Integer tariffId, Integer zoneId, Double weight);

    List<TariffDetail> findByDeletedAndService_IdAndTariff_IdAndZone_IdAndWeightGreaterThanOrderByWeightAsc(Integer i, Integer serviceId, Integer tariffId, Integer zoneId, Double weight);

    Iterable<TariffDetail> findByDeletedAndTariff_IdAndService_Id(Integer i, Integer id, Integer serviceId, Sort weight);
}
