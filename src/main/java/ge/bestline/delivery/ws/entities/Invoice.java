package ge.bestline.delivery.ws.entities;

import ge.bestline.delivery.ws.dto.InvoiceDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String identNumber;
    private String emailToSent;
    private String status;
    private String payStatus;
    private Double payedAmount;
    private Double amount;
    private String pdf;
    private Date operationDate;
    @ManyToMany(cascade = CascadeType.DETACH)
    private List<Parcel> parcels;
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedTime;
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Date createdTime;

    @PrePersist
    protected void onCreate() {
        createdTime = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedTime = new Date();
    }

    public Invoice(InvoiceDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.identNumber = dto.getIdentNumber();
        this.emailToSent = dto.getEmailToSent();
        this.status = dto.getStatus();
        this.payStatus = dto.getPayStatus();
        this.payedAmount = dto.getPayedAmount();
        this.amount = dto.getAmount();
        this.pdf = dto.getPdf();
        this.operationDate = dto.getOperationDate();
        this.parcels = dto.getParcels();
    }
}
