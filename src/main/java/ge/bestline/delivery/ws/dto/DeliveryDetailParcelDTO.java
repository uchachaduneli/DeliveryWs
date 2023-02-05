package ge.bestline.delivery.ws.dto;

import ge.bestline.delivery.ws.entities.ParcelStatusReason;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryDetailParcelDTO {
//    @JsonFormat(shape = JsonFormat.Shape.STRING, locale = "ka_GE", timezone = "Asia/Tbilisi", pattern = "yyyy-MM-dd@HH:mm")
    private Date deliveryTime;
    private String receiverName;
    private String statusNote;
    private String receiverIdentNumber;
    private ParcelStatusReason status;
    private Integer id;
}
