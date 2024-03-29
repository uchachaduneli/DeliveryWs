package ge.bestline.delivery.ws.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
public class Zone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer deleted;
    private Integer name;
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedTime;
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Date createdTime;

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
