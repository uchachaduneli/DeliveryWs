package ge.bestline.delivery.ws.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

// Reisi
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Tranzit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true)
    private String number;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    private Car car;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    private Route route;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    private User driver;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    private Warehouse senderWarehouse;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    private Warehouse destWarehouse;
    private java.sql.Date tranzitDate;
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
