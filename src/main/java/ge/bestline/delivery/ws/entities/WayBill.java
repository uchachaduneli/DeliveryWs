package ge.bestline.delivery.ws.entities;

import jdk.jfr.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import jakarta.persistence.*;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WayBill {
    @Id
    private String id;
    @ManyToOne(cascade = CascadeType.DETACH, optional = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private WayBillType type;
    @Timestamp
    private Date rsCreateDate;
    private String buyerTin;
    private String buyerName;
    private String startAddress;
    private String endAddress;
    private String driverTin;
    private String driverName;
    private Double transportCoast;
    @ManyToOne(cascade = CascadeType.DETACH, optional = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private WayBillStatus status;
    @Timestamp
    private Date activateDate;
    private Double fullAmount;
    private String carNumber;
    private String waybillNumber;
    private String syncStatus;
    private Integer sUserId;
    @Timestamp
    private Date beginDate;
    private String waybillComment;
    @Transient
    private String parsedWaybillComment;
    private Integer isConfirmed;
    private Integer isCorrected;
    private Integer buyerSt;
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedTime;
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Date createdTime;

    public WayBill(String id, String parsedWaybillComment) {
        this.id = id;
        this.parsedWaybillComment = parsedWaybillComment;
    }

    @PrePersist
    protected void onCreate() {
        createdTime = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedTime = new Date();
    }
}
