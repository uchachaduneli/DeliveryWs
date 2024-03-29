package ge.bestline.delivery.ws.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CourierCheckInOut {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String carNumber;
    private Integer odometer;
    private Integer distance;
    @NotNull
    private boolean isChekIn;
    @ManyToOne(cascade = CascadeType.PERSIST, optional = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private User courier;
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private Date operationTime;
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private Date updatedTime;
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private Date createdTime;

    @PrePersist
    protected void onCreate() {
        createdTime = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedTime = new Date();
    }
}
