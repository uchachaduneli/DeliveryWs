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
public class UserStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedTime;
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Date createdTime;

    public UserStatus(Integer id) {
        this.id = id;
    }

    public UserStatus(Integer id, String name) {
        this.id = id;
        this.name = name;
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
