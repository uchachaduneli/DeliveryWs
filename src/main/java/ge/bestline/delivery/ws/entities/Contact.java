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
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer deleted;
    private String name;
    private String email;
    private Integer type; // 1 personal / 2 juridical
    private Integer status; // 1 axali / 2 dzveli ?
    private Integer deReGe; // 1 ibegreba / 2 ar ibegreba ?
    private Integer hasContract; // 1 ara / 2 ki
    private String identNumber;
    @ManyToOne(cascade = CascadeType.DETACH, optional = false)
    private User user;
    @ManyToOne(cascade = CascadeType.DETACH, optional = false)
    private Tariff tariff;
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedTime;
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Date createdTime;

    public Contact(String name, Integer type, Integer status,
                   Integer deReGe, Integer hasContract,
                   String identNumber, User user, Tariff tariff) {
        this.name = name;
        this.type = type;
        this.status = status;
        this.deReGe = deReGe;
        this.hasContract = hasContract;
        this.identNumber = identNumber;
        this.user = user;
        this.tariff = tariff;
    }

    @PrePersist
    protected void onCreate() {
        deleted = 2;
        hasContract = 1;
        createdTime = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedTime = new Date();
    }
}
