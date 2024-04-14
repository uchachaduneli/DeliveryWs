package ge.bestline.delivery.ws.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @JsonIgnore
    private Integer deleted;
    private String subject;
    private String msg;
    @ManyToOne(cascade = CascadeType.DETACH)
    @NotNull
    private Warehouse to;
    @ManyToOne(cascade = CascadeType.DETACH)
    private User author;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne(cascade = CascadeType.DETACH)
    @NotNull
    private Parcel parcel;
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedTime;
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Date createdTime;

    public Message(String subject, String msg, Warehouse to, User author, Parcel parcel) {
        this.subject = subject;
        this.msg = msg;
        this.to = to;
        this.author = author;
        this.parcel = parcel;
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
