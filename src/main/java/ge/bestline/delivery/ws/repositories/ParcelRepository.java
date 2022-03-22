package ge.bestline.delivery.ws.repositories;

import ge.bestline.delivery.ws.entities.Parcel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ParcelRepository extends JpaRepository<Parcel, Integer> {
    List<Parcel> findByBarCodeIn(List<String> list);

    List<Parcel> findByIdIn(List<Integer> ides);
}
