package ge.bestline.delivery.ws.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ge.bestline.delivery.ws.entities.Message;
import ge.bestline.delivery.ws.entities.MessageCC;
import ge.bestline.delivery.ws.entities.ParcelStatusReason;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

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
