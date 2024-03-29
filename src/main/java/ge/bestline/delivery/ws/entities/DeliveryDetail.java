package ge.bestline.delivery.ws.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DeliveryDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotNull
    private Integer courierOrReception;// 1 courier 2 reception
    @Column(unique = true)
    private String detailBarCode;
    private String carNumber;
    @ManyToOne(cascade = CascadeType.DETACH)
    private Route route;
    @ManyToOne(cascade = CascadeType.DETACH)
    private Warehouse warehouse;
    @ManyToMany(cascade = CascadeType.DETACH)
    private List<Parcel> parcels;
    @ManyToOne(cascade = CascadeType.DETACH)
    private User user;
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
}
