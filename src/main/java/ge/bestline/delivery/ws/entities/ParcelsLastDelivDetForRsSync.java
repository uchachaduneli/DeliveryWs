package ge.bestline.delivery.ws.entities;

import ge.bestline.delivery.ws.dto.DeliveryDetailDTO;

import javax.persistence.*;

@Entity
@NamedNativeQuery(
        name = "findParcelsLatestDetailsPairsForCarNumbers_nat_query",
        query = "SELECT p.bar_code as parcelBarCode, ddt.car_number as carNumber, " +
                " concat(u.name,' ', u.last_name) AS courierDesc, u.personal_number as courierIdentNum " +
                " FROM delivery_detail_parcels ddp " +
                "         JOIN delivery_detail ddt ON ddp.delivery_detail_id = ddt.id " +
                "         JOIN parcel p ON ddp.parcels_id = p.id " +
                "         JOIN `user` u ON ddt.user_id=u.id " +
                "WHERE ddt.car_number IS NOT NULL " +
                "  AND p.bar_code IN (:barcodes) " +
                "  AND ddt.id = (SELECT MAX(dd.id) " +
                "                FROM delivery_detail dd " +
                "                         JOIN delivery_detail_parcels dp ON dp.delivery_detail_id = dd.id " +
                "                WHERE dp.parcels_id = p.id " +
                "                GROUP BY dp.parcels_id)",
        resultSetMapping = "parcel_last_del_det_dto"
)
@SqlResultSetMapping(
        name = "parcel_last_del_det_dto",
        classes = @ConstructorResult(
                targetClass = DeliveryDetailDTO.class,
                columns = {
                        @ColumnResult(name = "parcelBarCode", type = String.class),
                        @ColumnResult(name = "carNumber", type = String.class),
                        @ColumnResult(name = "courierDesc", type = String.class),
                        @ColumnResult(name = "courierIdentNum", type = String.class)
                }
        )
)
public class ParcelsLastDelivDetForRsSync {
    @Id
    private String parcelBarCode;
}
