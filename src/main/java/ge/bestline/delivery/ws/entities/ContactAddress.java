package ge.bestline.delivery.ws.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ContactAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(cascade = CascadeType.DETACH)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull
    Contact contact;
    @ManyToOne
    private City city;
    private String postCode;
    private String street;
    private String appartmentDetails; // flat N, floor ..
    private String contactPerson;
    private String contactPersonPhone;
    private String contactPersonEmail;
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

    public ContactAddress(Contact contact, City city, String postCode, String street, String appartmentDetails, String contactPerson, String contactPersonPhone, String contactPersonEmail) {
        this.contact = contact;
        this.city = city;
        this.postCode = postCode;
        this.street = street;
        this.appartmentDetails = appartmentDetails;
        this.contactPerson = contactPerson;
        this.contactPersonPhone = contactPersonPhone;
        this.contactPersonEmail = contactPersonEmail;
    }
}
