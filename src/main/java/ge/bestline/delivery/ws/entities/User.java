package ge.bestline.delivery.ws.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@Entity
@RequiredArgsConstructor
@ToString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer deleted;
    private Integer parentUserId;
    private String printerMacAddress;
    private String firebaseToken;
    private String name;
    private String lastName;
    @Column(unique = true)
    private String userName;
    private String password;
    private String phone;
    @Column(unique = true)
    private String personalNumber;
    @ManyToOne(cascade = CascadeType.DETACH)
    private City city;
    @ManyToOne(cascade = CascadeType.DETACH)
    private Warehouse warehouse;
    @ManyToOne(cascade = CascadeType.DETACH)
    private Route route;
    @ManyToMany(cascade = CascadeType.DETACH)
    private Set<Role> role;
    @ManyToOne(cascade = CascadeType.DETACH)
    private UserStatus status;
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedTime;
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Date createdTime;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Transient
    private List<String> srchRoleName; // Not DB Field

    public User(String name, String lastName, String phone, String personalNumber, City city, Set<Role> role, UserStatus status) {
        this.name = name;
        this.lastName = lastName;
        this.phone = phone;
        this.personalNumber = personalNumber;
        this.city = city;
        this.role = role;
        this.status = status;
    }

    public User(Integer id) {
        this.id = id;
    }

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

    public boolean hasRole(String roleName) {
        for (Role r : role) {
            if (r.getName().equals(roleName)) {
                return true;
            }
        }
        return false;
    }
}
