package ge.bestline.delivery.ws.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ParcelDTO {
    private Integer id;
    private Integer deleted;
    // 1 - pre inserted with empty values, will be filled after some time
    private boolean prePrinted;
    private String barCode;

    private Integer senderId;
    private String senderName;
    private String senderIdentNumber;
    private String senderContactPerson;
    private String senderAddress;
    private String senderPhone;
    private Integer senderCityId;
    private Integer sendSmsToSender;

    private Integer receiverId;
    private String receiverName;
    private String receiverIdentNumber;
    private String receiverContactPerson;
    private String receiverAddress;
    private String receiverPhone;
    private Integer receiverCityId;
    private Integer sendSmsToReceiver;

    private String deliveredToPers;
    private String deliveredToPersIdent;
    private String deliveredToPersRelativeLevel;
    private String deliveredToPersNote;
    private String deliveredToPersSignature;
    private String deliveredParcelimage;

    private Integer payerId;
    private Integer payerSide; // 1 sender  2 receiver   3 third side
    private String payerName;
    private String payerIdentNumber;
    private String payerAddress;
    private String payerPhone;
    private String payerContactPerson;
    private Integer payerCityId;

    private Integer statusId;
    private Integer statusReasonId;
    private String statusNote; // insert/update operations ar made from deliveryDetails Page
//    private Date statusDateTime; //xelit sheyavt statusebismenejeris feijze

    private String comment;
    private Integer deliveredConfirmation; //1 yes 2 no
    private Integer count;
    private Double weight;
    private Double weightTo;
    private Double volumeWeight;
    private Double volumeWeightTo;
    private Double gadafutvisPrice;
    private Double totalPrice;
    private Double totalPriceTo;
    private Integer deliveryType;// 1 mitana misamartze, 2 mikitxva filialshi
    private Integer paymentType;// 1 invoice, 2 cash, 3 card
    private Integer serviceId;
    private Integer packageType;// 1 amanati, 2 paketi
    private Integer routeId;
    private Integer courierId;
    private Integer authorId;
    private Double tariff;
    private String content;
    private Date deliveryTime;
    private Date deliveryTimeTo;
    private Date createdTime;
    private Date createdTimeTo;
    private String strDeliveryTime;
    private String strDeliveryTimeTo;
    private String strCreatedTime;
    private String strCreatedTimeTo;
    private boolean addedFromGlobal;

    public static Timestamp convertStrDateToDateObj(String strDate) throws ParseException {
        Date tmpDate = (Date) new SimpleDateFormat("yyyy-MM-dd HH:mm")
                .parse(strDate.replace("T", " "));
        return new Timestamp(tmpDate.getTime());
    }
}
