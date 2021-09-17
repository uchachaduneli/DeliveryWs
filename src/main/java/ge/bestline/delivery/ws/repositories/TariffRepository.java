package ge.bestline.delivery.ws.repositories;

import ge.bestline.delivery.ws.entities.Tariff;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TariffRepository extends JpaRepository<Tariff, Integer> {
}
