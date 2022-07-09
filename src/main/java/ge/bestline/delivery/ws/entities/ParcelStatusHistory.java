package ge.bestline.delivery.ws.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ParcelStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String reason;
    private String code;
    private Timestamp statusDateTime;
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Date createdTime;
    @ManyToOne(cascade = CascadeType.DETACH)
    private Parcel parcel;

    public ParcelStatusHistory(Parcel parcel, String name, String code, String reason) {
        this.parcel = parcel;
        this.reason = reason;
        this.name = name;
        this.code = code;
    }

    public ParcelStatusHistory(Parcel parcel, String name, String code, String reason, Timestamp statusDateTime) {
        this.parcel = parcel;
        this.reason = reason;
        this.name = name;
        this.code = code;
        this.statusDateTime = statusDateTime;
    }

    @PrePersist
    protected void onCreate() {
        createdTime = new Date();
    }

}
