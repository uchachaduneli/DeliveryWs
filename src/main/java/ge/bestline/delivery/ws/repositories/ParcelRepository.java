package ge.bestline.delivery.ws.repositories;

import ge.bestline.delivery.ws.entities.Parcel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParcelRepository extends JpaRepository<Parcel, Integer> {
}
