package ge.bestline.delivery.ws.dao;

import ge.bestline.delivery.ws.dto.ParcelDTO;
import ge.bestline.delivery.ws.entities.Parcel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.HashMap;
import java.util.Map;

@Repository
public class ParcelDao {
    EntityManager em;

    public ParcelDao(EntityManager em) {
        this.em = em;
    }

    public Map<String, Object> findAll(int page, int rowCount, ParcelDTO obj, boolean needTotalPriceSum) {
        Map<String, Object> response = new HashMap<>();
        StringBuilder q = new StringBuilder();
        q.append(" From ").append(Parcel.class.getSimpleName()).append(" e Where e.deleted=2 ");

        if (obj.getAuthorId() != null) {
            q.append(" and e.author.id ='").append(obj.getAuthorId()).append("'");
        }

        if (obj.getId() != null && obj.getId() > 0) {
            q.append(" and e.id ='").append(obj.getId()).append("'");
        }
        if (obj.isPrePrinted()) {
            q.append(" and e.prePrinted ='").append(obj.isPrePrinted() ? 1 : 0).append("'");
        }
        if (obj.getSenderId() != null && obj.getSenderId() > 0) {
            q.append(" and e.senderId ='").append(obj.getSenderId()).append("'");
        }

        if (obj.getRouteId() != null) {
            q.append(" and e.route.id ='").append(obj.getRouteId()).append("'");
        }

        if (obj.getInvoiced() != null) {
            q.append(" and e.invoiced ='").append(obj.getInvoiced().booleanValue()).append("'");
        }

        if (StringUtils.isNotBlank(obj.getBarCode())) {
            q.append(" and e.barCode ='").append(obj.getBarCode().trim()).append("'");
        }
        if (StringUtils.isNotBlank(obj.getSenderName())) {
            q.append(" and e.senderName like '").append(obj.getSenderName().trim()).append("%'");
        }
        if (StringUtils.isNotBlank(obj.getSenderIdentNumber())) {
            q.append(" and e.senderIdentNumber like '").append(obj.getSenderIdentNumber()).append("%'");
        }
        if (StringUtils.isNotBlank(obj.getSenderPhone())) {
            q.append(" and e.senderPhone like '%").append(obj.getSenderPhone()).append("%'");
        }
        if (StringUtils.isNotBlank(obj.getSenderAddress())) {
            q.append(" and e.senderAddress like '%").append(obj.getSenderAddress()).append("%'");
        }
        if (obj.getSenderCityId() != null) {
            q.append(" and e.senderCity.id ='").append(obj.getSenderCityId()).append("'");
        }
        if (obj.getReceiverId() != null && obj.getReceiverId() > 0) {
            q.append(" and e.receiverId ='").append(obj.getReceiverId()).append("'");
        }
        if (StringUtils.isNotBlank(obj.getReceiverName())) {
            q.append(" and e.receiverName like '").append(obj.getReceiverName()).append("%'");
        }
        if (StringUtils.isNotBlank(obj.getReceiverIdentNumber())) {
            q.append(" and e.receiverIdentNumber like '").append(obj.getReceiverIdentNumber()).append("%'");
        }
        if (StringUtils.isNotBlank(obj.getReceiverPhone())) {
            q.append(" and e.receiverPhone like '%").append(obj.getReceiverPhone()).append("%'");
        }
        if (StringUtils.isNotBlank(obj.getReceiverAddress())) {
            q.append(" and e.receiverAddress like '%").append(obj.getReceiverAddress()).append("%'");
        }
        if (obj.getReceiverCityId() != null) {
            q.append(" and e.receiverCity.id ='").append(obj.getReceiverCityId()).append("'");
        }

        if (StringUtils.isNotBlank(obj.getDeliveredToPers())) {
            q.append(" and e.deliveredToPers like '%").append(obj.getDeliveredToPers()).append("%'");
        }
        if (StringUtils.isNotBlank(obj.getDeliveredToPersIdent())) {
            q.append(" and e.deliveredToPersIdent like '").append(obj.getDeliveredToPersIdent()).append("%'");
        }


        if (obj.getPayerId() != null && obj.getPayerId() > 0) {
            q.append(" and e.payerId ='").append(obj.getPayerId()).append("'");
        }

        if (StringUtils.isNotBlank(obj.getPayerIdentNumber())) {
            q.append(" and e.payerIdentNumber = '").append(obj.getPayerIdentNumber()).append("'");
        }
        if (StringUtils.isNotBlank(obj.getPayerPhone())) {
            q.append(" and e.payerPhone like '%").append(obj.getPayerPhone()).append("%'");
        }
        if (StringUtils.isNotBlank(obj.getPayerAddress())) {
            q.append(" and e.payerAddress like '%").append(obj.getPayerAddress()).append("%'");
        }
        if (obj.getPayerCityId() != null) {
            q.append(" and e.payerCity.id ='").append(obj.getPayerCityId()).append("'");
        }
        //if status reasonID is presented filter with it else use this heavy condition
        if (obj.getStatusId() != null && obj.getStatusId() > 0
                && (obj.getStatusReasonId() == null || obj.getStatusReasonId() == 0)) {
            q.append(" and e.status.status.id ='").append(obj.getStatusId()).append("'");
        }
        if (obj.getStatusReasonId() != null && obj.getStatusReasonId() > 0) {
            q.append(" and e.status.id ='").append(obj.getStatusReasonId()).append("'");
        }

        if (StringUtils.isNotBlank(obj.getStatusNote())) {
            q.append(" and e.statusNote like '%").append(obj.getStatusNote()).append("%'");
        }

        if (StringUtils.isNotBlank(obj.getComment())) {
            q.append(" and e.comment like'%").append(obj.getComment()).append("%'");
        }

        if (obj.getDeliveredConfirmation() != null && obj.getDeliveredConfirmation() > 0) {
            q.append(" and e.deliveredConfirmation ='").append(obj.getDeliveredConfirmation()).append("'");
        }
        if (obj.getCount() != null && obj.getCount() > 0) {
            q.append(" and e.count ='").append(obj.getCount()).append("'");
        }
        if (obj.getWeight() != null && obj.getWeight() > 0) {
            q.append(" and e.weight ='").append(obj.getWeight()).append("'");
        }
        if (obj.getTotalPrice() != null && obj.getTotalPrice() > 0) {
            q.append(" and e.totalPrice ='").append(obj.getTotalPrice()).append("'");
        }
        if (obj.getDeliveryType() != null && obj.getDeliveryType() > 0) {
            q.append(" and e.deliveryType ='").append(obj.getDeliveryType()).append("'");
        }
        if (obj.getPaymentType() != null && obj.getPaymentType() > 0) {
            q.append(" and e.paymentType ='").append(obj.getPaymentType()).append("'");
        }
        if (obj.getPackageType() != null && obj.getPackageType() > 0) {
            q.append(" and e.packageType ='").append(obj.getPackageType()).append("'");
        }

        if (obj.getServiceId() != null) {
            q.append(" and e.service.id ='").append(obj.getServiceId()).append("'");
        }
        if (obj.getRouteId() != null) {
            q.append(" and e.route.id ='").append(obj.getRouteId()).append("'");
        }
        if (obj.getAddedFromGlobal() != null) {
            q.append(" and e.addedFromGlobal ='").append(obj.getAddedFromGlobal().booleanValue()).append("'");
        }

        if (obj.getCourierId() != null) {
            q.append(" and e.courier.id ='").append(obj.getCourierId()).append("'");
        }

        if (obj.getTariff() != null && obj.getTariff() > 0) {
            q.append(" and e.tariff ='").append(obj.getTariff()).append("'");
        }

        if (StringUtils.isNotBlank(obj.getContent())) {
            q.append(" and e.content like '%").append(obj.getContent()).append("%'");
        }

        if (obj.getCreatedTime() != null && obj.getCreatedTimeTo() != null) {
            q.append(" and ( e.createdTime between '").append(obj.getCreatedTime()).append("' and '")
                    .append(obj.getCreatedTimeTo()).append("') ");
        } else {
            if (obj.getCreatedTime() != null) {
                q.append(" and e.createdTime >'").append(obj.getCreatedTime()).append("'");
            }
            if (obj.getCreatedTimeTo() != null) {
                q.append(" and e.createdTime <'").append(obj.getCreatedTimeTo()).append("'");
            }
        }

        if (obj.getDeliveryTime() != null && obj.getDeliveryTimeTo() != null) {
            q.append(" and ( e.deliveryTime between '").append(obj.getDeliveryTime()).append("' and '")
                    .append(obj.getDeliveryTimeTo()).append("') ");
        } else {
            if (obj.getDeliveryTime() != null) {
                q.append(" and e.deliveryTime >'").append(obj.getDeliveryTime()).append("'");
            }
            if (obj.getDeliveryTimeTo() != null) {
                q.append(" and e.deliveryTime <'").append(obj.getDeliveryTimeTo()).append("'");
            }
        }

        if (obj.getWeight() != null && obj.getWeightTo() != null) {
            q.append(" and e.weight >='").append(obj.getWeight()).append("' and e.weight <='")
                    .append(obj.getWeightTo()).append("' ");
        } else {
            if (obj.getDeliveryTime() != null) {
                q.append(" and e.weight ='").append(obj.getDeliveryTime()).append("'");
            }
            if (obj.getDeliveryTimeTo() != null) {
                q.append(" and e.weight ='").append(obj.getDeliveryTimeTo()).append("'");
            }
        }
        if (obj.getVolumeWeight() != null && obj.getVolumeWeightTo() != null) {
            q.append(" and e.volumeWeight >='").append(obj.getVolumeWeight()).append("' and e.weight <='")
                    .append(obj.getVolumeWeightTo()).append("' ");
        } else {
            if (obj.getDeliveryTime() != null) {
                q.append(" and e.volumeWeight ='").append(obj.getDeliveryTime()).append("'");
            }
            if (obj.getDeliveryTimeTo() != null) {
                q.append(" and e.volumeWeight ='").append(obj.getDeliveryTimeTo()).append("'");
            }
        }

        TypedQuery<Parcel> query = em.createQuery("SELECT e " + q.toString() + " order by e.id desc", Parcel.class);
        TypedQuery<Long> cntQr = em.createQuery("SELECT count(1) " + q.toString(), Long.class);
        response.put("items", query.setFirstResult(page * rowCount).setMaxResults(rowCount).getResultList());
        response.put("total_count", cntQr.getSingleResult());
        if (needTotalPriceSum) {
            response.put("total_price_sum", em.createQuery("SELECT sum(e.totalPrice) " + q.toString(), Double.class).getSingleResult());
        }
        return response;
    }
}
