package ge.bestline.delivery.ws.dto;

import ge.bestline.delivery.ws.entities.Parcel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryDetailDTO {
    private Integer id;
    private String detailBarCode;
    private String parcelBarCode;
    private Integer routeId;
    private Integer warehouseId;
    private List<Parcel> parcels;
    private Integer userId;
    private Date createdTime;
    private Date createdTimeTo;
    private String strCreatedTime;
    private String strCreatedTimeTo;
}
