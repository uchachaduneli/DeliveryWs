package ge.bestline.delivery.ws.repositories;

import ge.bestline.delivery.ws.entities.Parcel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParcelRepository extends JpaRepository<Parcel, Integer> {
    List<Parcel> findByBarCodeIn(List<String> list);

    List<Parcel> findByIdIn(List<Integer> ides);

    Optional<Parcel> findByBarCode(String barCode);

    List<Parcel> findBySenderIdentNumber(String personalNumber);

    List<Parcel> findByPayerIdentNumberAndDeletedAndInvoiced(String personalNumber, Integer deleted, boolean invoiced);

}
