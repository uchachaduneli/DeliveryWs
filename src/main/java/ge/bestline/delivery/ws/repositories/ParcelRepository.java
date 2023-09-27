package ge.bestline.delivery.ws.repositories;

import ge.bestline.delivery.ws.entities.Parcel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ParcelRepository extends JpaRepository<Parcel, Integer> {
    List<Parcel> findByBarCodeInAndDeleted(List<String> list, Integer deleted);

    List<Parcel> findByIdInAndDeleted(List<Integer> ides, Integer deleted);

    Optional<Parcel> findByBarCodeAndDeleted(String barCode, Integer deleted);

    List<Parcel> findBySenderIdentNumberAndDeleted(String personalNumber, Integer deleted);

    Page<Parcel> findByPayerIdentNumberAndDeletedAndInvoiced(String identNumber, int i, boolean b, Pageable paging);

    List<Parcel> findByBarCodeInAndDeletedAndStatusIdIn(Set<String> barcodesFromWaybillComments, int i, Set<Integer> statusReasonsIdes);
}
