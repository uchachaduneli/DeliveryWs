package ge.bestline.delivery.ws.entities;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@RequiredArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String lastName;
    private String phone;
    private String personalNumber;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    private City city;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    private Route route;
    private String role;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    private UserStatus status;
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedTime;
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Date createdTime;

    @PrePersist
    public void prePersist() {
        createdTime = new Date();
        if (status == null) {
            status = new UserStatus(1);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedTime = new Date();
    }
}
