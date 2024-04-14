package ge.bestline.delivery.ws.entities;

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
public class ContactAddress {
    @ManyToOne(cascade = CascadeType.DETACH)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull
    Contact contact;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    private City city;
    private String postCode;
    private String street;
    private String appartmentDetails; // flat N, floor ..
    private String contactPerson;
    private String contactPersonPhone;
    private String contactPersonEmail;
    private Integer isPayAddress; //1 ki 2 ara
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedTime;
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
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
