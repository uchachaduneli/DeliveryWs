package ge.bestline.delivery.ws.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Bag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true)
    private String barCode;
    @ManyToOne(cascade = CascadeType.DETACH, optional = false)
    private Warehouse from;
    @ManyToOne(cascade = CascadeType.DETACH, optional = false)
    private Warehouse to;
    @ManyToOne(cascade = CascadeType.DETACH)
    private ParcelStatusReason status;
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Parcel> parcels;
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
