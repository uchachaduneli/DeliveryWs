package ge.bestline.delivery.ws.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Parcel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @JsonIgnore
    private Integer deleted;

    private String senderName;
    private String senderIdentNumber;
    private String senderContactPerson;
    private String senderAddress;
    @ManyToOne(cascade = CascadeType.DETACH)
    private City senderCity;

    private String receiverName;
    private String receiverIdentNumber;
    private String receiverContactPerson;
    private String receiverAddress;
    @ManyToOne(cascade = CascadeType.DETACH)
    private City receiverCity;

    private Integer payerSide; // 1 sender  2 receiver   3 third side
    private String payerName;
    private String payerIdentNumber;
    private String payerAddress;
    private String payerContactPerson;
    @ManyToOne(cascade = CascadeType.DETACH)
    private City payerCity;

    @ManyToOne(cascade = CascadeType.DETACH)
    private ParcelStatus status;

    private String comment;
    private Integer deliveredConfirmation; //1 yes 2 no
    private Integer count;
    private Double weight;
    private Double volumeWeight;
    private Double gadafutvisPrice;
    private Double totalPrice;
    private Integer deliveryType;// 1 mitana misamartze, 2 mikitxva filialshi
    private Integer paymentType;// 1 invoice, 2 cash, 3 card
    @ManyToOne(cascade = CascadeType.DETACH)
    private Services parcelType;
    private Integer packageType;// 1 amanati, 2 paketi
    @ManyToOne(cascade = CascadeType.DETACH)
    private DocType sticker;
    @ManyToOne(cascade = CascadeType.DETACH)
    private Route route;
    @ManyToOne(cascade = CascadeType.DETACH, optional = true)
    private User courier;
    @ManyToOne(cascade = CascadeType.DETACH, optional = true)
    private User author;
    private String content;
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedTime;
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Date createdTime;

    @PrePersist
    protected void onCreate() {
        deleted = 2;
        if (status == null) {
            status = new ParcelStatus(1);
        }
        createdTime = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedTime = new Date();
    }
}
