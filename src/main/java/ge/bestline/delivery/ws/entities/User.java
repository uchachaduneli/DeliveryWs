package ge.bestline.delivery.ws.entities;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Data
@Entity
@RequiredArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer deleted;
    private String name;
    private String lastName;
    private String phone;
    private String personalNumber;
    @OneToOne(cascade = CascadeType.DETACH)
    private City city;
    @OneToOne(cascade = CascadeType.DETACH)
    private Route route;
    @ManyToMany(cascade = CascadeType.DETACH)
    private Set<Role> role;
    @OneToOne(cascade = CascadeType.DETACH)
    private UserStatus status;
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedTime;
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Date createdTime;

    @PrePersist
    public void prePersist() {
        deleted = 2;
        createdTime = new Date();
        if (status == null) {
            status = new UserStatus(1);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedTime = new Date();
    }

    public User(String name, String lastName, String phone, String personalNumber, City city, Set<Role> role, UserStatus status) {
        this.name = name;
        this.lastName = lastName;
        this.phone = phone;
        this.personalNumber = personalNumber;
        this.city = city;
        this.role = role;
        this.status = status;
    }
}
