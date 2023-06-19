package ge.bestline.delivery.ws.dto;

import ge.bestline.delivery.ws.entities.CourierCheckInOut;
import lombok.Data;

import java.util.Date;

@Data
public class CourierCheckInOutDTO extends CourierCheckInOut {
    private Integer isCheckinParam;
    private Integer odometerTo;
    private String strOperationTime;
    private Date operationTimeTo;
    private String strOperationTimeTo;
    private String strCreatedTime;
    private Date createTimeTo;
    private String strCreatedTimeTo;
}
