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
public class ParcelStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String code;
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedTime;
    private String category;
    private String parcelStatusOnChekpoint;
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Date createdTime;
    private Integer deleted;

    public ParcelStatus(Integer id) {
        this.id = id;
    }

    public ParcelStatus(Integer id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }

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
