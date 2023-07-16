package ge.bestline.delivery.ws.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ParcelStatusReason {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String code;
    @ManyToOne(cascade = CascadeType.DETACH)
    private ParcelStatus status;
    private String category;
    private String parcelStatusOnChekpoint;
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedTime;
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Date createdTime;
    @Column(columnDefinition = "boolean default false")
    private Boolean showInMobail;

    public ParcelStatusReason(Integer id) {
        this.id = id;
    }

    public ParcelStatusReason(Integer id, ParcelStatus status, String name) {
        this.id = id;
        this.name = name;
        this.status = status;
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
