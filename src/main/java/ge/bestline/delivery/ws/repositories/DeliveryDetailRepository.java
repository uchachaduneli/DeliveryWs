package ge.bestline.delivery.ws.repositories;

import ge.bestline.delivery.ws.dto.DeliveryDetailDTO;
import ge.bestline.delivery.ws.entities.DeliveryDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DeliveryDetailRepository extends JpaRepository<DeliveryDetail, Integer> {
    Optional<DeliveryDetail> findByDetailBarCode(String barCode);

    // findParcelsLatestDetailsPairsForCarNumbers_nat_query    - es queri entitiebshi naxe ParcelsLastDelivDetForRsSync.java -shi
    @Query(nativeQuery = true, name = "findParcelsLatestDetailsPairsForCarNumbers_nat_query")
    List<DeliveryDetailDTO> findParcelsLatestDetailsPairsForCarNumbers(@Param("barcodes") List<String> parcelBarCodes);
}
