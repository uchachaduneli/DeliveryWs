package ge.bestline.delivery.ws.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import java.sql.Timestamp;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ParcelStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String reason;
    private String code;
    private Date statusDateTime;
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Date createdTime;
    @ManyToOne(cascade = CascadeType.DETACH)
    private Parcel parcel;

    @ManyToOne(cascade = CascadeType.DETACH, optional = true)
    @NotFound(action = NotFoundAction.IGNORE)
    private User operUSer;

    public ParcelStatusHistory(Parcel parcel, String name, String code, String reason, Timestamp statusDateTime, User operUSer) {
        this.parcel = parcel;
        this.reason = reason;
        this.name = name;
        this.code = code;
        this.statusDateTime = statusDateTime;
        this.operUSer = operUSer;
    }

    @PrePersist
    protected void onCreate() {
        createdTime = new Date();
    }

}
