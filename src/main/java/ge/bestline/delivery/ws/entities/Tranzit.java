package ge.bestline.delivery.ws.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

// Reisi
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Tranzit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer deleted;
    @Column(unique = true)
    private String number;
    @ManyToOne(cascade = CascadeType.DETACH)
    private Car car;
    @ManyToOne(cascade = CascadeType.DETACH)
    private ParcelStatusReason status;
    @ManyToOne(cascade = CascadeType.DETACH)
    private City routeFrom;
    @ManyToOne(cascade = CascadeType.DETACH)
    private City routeTo;
    @ManyToOne(cascade = CascadeType.DETACH)
    private User driver;
    @ManyToOne(cascade = CascadeType.DETACH)
    private Warehouse senderWarehouse;
    @ManyToOne(cascade = CascadeType.DETACH)
    private Warehouse destWarehouse;
    @ManyToMany(cascade = CascadeType.DETACH)
    private List<Bag> bags;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedTime;
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Date createdTime;
    private java.sql.Date tranzitDate;

    @PrePersist
    protected void onCreate() {
        deleted = 2;
        createdTime = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedTime = new Date();
    }
}
