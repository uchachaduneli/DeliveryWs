package ge.bestline.delivery.ws.dto;

import ge.bestline.delivery.ws.entities.ParcelStatus;
import ge.bestline.delivery.ws.entities.ParcelStatusReason;
import lombok.Data;

import java.util.List;

@Data
public class ParcelStatusWithReasonsDTO {
    private ParcelStatus status;
    private List<ParcelStatusReason> reasonList;

    public ParcelStatusWithReasonsDTO(ParcelStatus status, List<ParcelStatusReason> reasonList) {
        this.status = status;
        this.reasonList = reasonList;
    }
}
