package ge.bestline.delivery.ws.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Date;

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
    // 1 - pre inserted with empty values, will be filled after some time
    private Integer prePrinted;
    @Column(unique = true, updatable = false)
    private String barCode;

    private Integer senderId;
    private String senderName;
    private String senderIdentNumber;
    private String senderContactPerson;
    private String senderAddress;
    private String senderPhone;
    @ManyToOne(cascade = CascadeType.DETACH)
    @NotFound(action = NotFoundAction.IGNORE)
    private City senderCity;
    private Integer sendSmsToSender;

    private Integer receiverId;
    private String receiverName;
    private String receiverIdentNumber;
    private String receiverContactPerson;
    private String receiverAddress;
    private String receiverPhone;

    private String deliveredToPers;
    private String deliveredToPersIdent;
    private String deliveredToPersRelativeLevel;
    private String deliveredToPersNote;
    private String deliveredToPersSignature;
    private String deliveredParcelimage;
    @ManyToOne(cascade = CascadeType.DETACH)
    @NotFound(action = NotFoundAction.IGNORE)
    private City receiverCity;
    private Integer sendSmsToReceiver;

    private Integer payerId;
    private Integer payerSide; // 1 sender  2 receiver   3 third side
    private String payerName;
    private String payerIdentNumber;
    private String payerAddress;
    private String payerPhone;
    private String payerContactPerson;
    @ManyToOne(cascade = CascadeType.DETACH)
    @NotFound(action = NotFoundAction.IGNORE)
    private City payerCity;

    @ManyToOne(cascade = CascadeType.DETACH)
    private ParcelStatusReason status;
    private String statusNote; // insert/update operations ar made from deliveryDetails Page
    private java.sql.Timestamp statusDateTime; //xelit sheyavt statusebismenejeris feijze
    private Integer courierStatus; //1 = Seen else Not Seen - on mobile device

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
    @NotFound(action = NotFoundAction.IGNORE)
    private Services service;
    private Integer packageType;// 1 amanati, 2 paketi
    @ManyToOne(cascade = CascadeType.DETACH)
    @NotFound(action = NotFoundAction.IGNORE)
    private Route route;
    @ManyToOne(cascade = CascadeType.DETACH, optional = true)
    @JoinColumn(name = "courier_id")
    @NotFound(action = NotFoundAction.IGNORE)
    private User courier;
    @ManyToOne(cascade = CascadeType.DETACH, optional = true)
    @JoinColumn(name = "author_id")
    @NotFound(action = NotFoundAction.IGNORE)
    private User author;
    private Double tariff;
    private String content;
    private Date deliveryTime;
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedTime;
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Date createdTime;

    public Parcel(String barCode) {
        this.barCode = barCode;
        this.prePrinted = 1;
    }

    @PrePersist
    protected void onCreate() {
        deleted = 2;
        if (status == null) {
            status = new ParcelStatusReason(1);
        }
        createdTime = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedTime = new Date();
    }

    public Parcel(ExcelTmpParcel obj) {
        if (obj.getSender() != null) {
            this.payerId = obj.getSender().getId();
            this.senderName = obj.getSender().getName();
            this.senderIdentNumber = obj.getSender().getIdentNumber();
            this.payerName = this.senderName;
            this.payerIdentNumber = this.senderIdentNumber;
        }

        this.senderCity = obj.getSenderCity();
        this.senderPhone = obj.getSenderPhone();
        this.senderContactPerson = obj.getSenderContactPerson();
        this.senderAddress = obj.getSenderAddress();

        //for imported excel payer is sender
        this.payerSide = 1;
        this.payerCity = this.senderCity;
        this.payerPhone = this.senderPhone;
        this.payerContactPerson = this.senderContactPerson;
        this.payerAddress = this.senderAddress;

        this.setService(obj.getService());
        this.barCode = obj.getBarCode();
        this.receiverName = obj.getReceiverName();
        this.receiverIdentNumber = obj.getReceiverIdentNumber();
        this.receiverContactPerson = obj.getReceiverContactPerson();
        this.receiverAddress = obj.getReceiverAddress();
        this.receiverPhone = obj.getReceiverPhone();
        this.receiverCity = obj.getReceiverCity();
        this.comment = obj.getComment();
        this.count = obj.getCount();
        this.weight = obj.getWeight();
        this.totalPrice = obj.getTotalPrice();
        this.route = obj.getRoute();
        this.author = obj.getAuthor();
        this.content = obj.getContent();
    }
}
