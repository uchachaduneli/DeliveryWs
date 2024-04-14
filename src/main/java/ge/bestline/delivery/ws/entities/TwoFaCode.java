package ge.bestline.delivery.ws.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TwoFaCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true)
    private String code;
    private String phone;
    private boolean expired;
    private boolean used;
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedTime;
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Date createdTime;

    public TwoFaCode(String code, String phone) {
        this.code = code;
        this.phone = phone;
    }

    @PrePersist
    protected void onCreate() {
        createdTime = new Date();
        expired = false;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedTime = new Date();
    }
}
