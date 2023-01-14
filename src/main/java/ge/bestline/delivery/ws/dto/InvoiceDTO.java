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
public class InvoiceDTO {
    private Integer id;
    private String name;
    private String identNumber;
    private String status;
    private String payStatus;
    private String pdf;
    private Date operationDate;
    private Date operationDateTo;
    private String strOperationDate;
    private String strOperationDateTo;
    private List<Parcel> parcels;
    private long parcelsCount;

    // for invoice generation page list
    public InvoiceDTO(String name, String identNumber, long parcelsCount) {
        this.name = name;
        this.identNumber = identNumber;
        this.parcelsCount = parcelsCount;
    }
}
