package ge.bestline.delivery.ws.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ExcelTmpParcel {

    @Transient
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    Integer rowIndex;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true)
    private String barCode;
    // new timestamp to long. Is same for every row
    @Column(nullable = false)
    private Long tmpIdForPerExcel;

    @ManyToOne(cascade = CascadeType.DETACH)
    private Services service;

    @ManyToOne(cascade = CascadeType.DETACH)
    private Contact sender;
    // es sami veli sheidzleba shecvlili movides reqvestshi magitoa sender- is garet
    private String senderContactPerson;
    private String senderAddress;
    private String senderPhone;
    @ManyToOne(cascade = CascadeType.DETACH)
    private City senderCity;

    private String receiverName;
    private String receiverIdentNumber;
    private String receiverContactPerson;
    private String receiverAddress;
    private String receiverPhone;
    @ManyToOne(cascade = CascadeType.DETACH)
    private City receiverCity;

    // payer side is always sender for excell imported parcels

    private String comment;
    private Integer count;
    private Double weight;
    private Double totalPrice;

    @ManyToOne(cascade = CascadeType.DETACH)
    private Route route;
    @ManyToOne(cascade = CascadeType.DETACH, optional = false)
    private User author;
    private String content;
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedTime;
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Date createdTime;

    @PreUpdate
    protected void onUpdate() {
        updatedTime = new Date();
    }
}
