package ge.bestline.delivery.ws.dto;

import ge.bestline.delivery.ws.entities.Parcel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDTO {
    private Integer id;
    private String name;
    private String identNumber;
    private String emailToSent;
    private String status;
    private String payStatus;
    private String pdf;
    private Date operationDate;
    private Date operationDateTo;
    private String strOperationDate;
    private String strOperationDateTo;
    private List<Parcel> parcels;
    private long parcelsCount;
    private Double amount;
    private Double payedAmount;
    private String payerEmail;


    // for invoice generation page list
    public InvoiceDTO(String name, String identNumber, long parcelsCount, Double amount) {
        this.name = name;
        this.identNumber = identNumber;
        this.parcelsCount = parcelsCount;
        this.amount = amount;
    }

    public static Timestamp convertStrDateToDateObj(String strDate) throws ParseException {
        Date tmpDate = (Date) new SimpleDateFormat("yyyy-MM-dd HH:mm")
                .parse(strDate.replace("T", " "));
        return new Timestamp(tmpDate.getTime());
    }
}
