package ge.bestline.delivery.ws.dao;

import ge.bestline.delivery.ws.entities.DeliveryDetail;
import ge.bestline.delivery.ws.entities.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DeliveryDetailDao {
    EntityManager em;

    public DeliveryDetailDao(EntityManager em) {
        this.em = em;
    }

    public Map<String, Object> findAll(int page, int rowCount, DeliveryDetail srchRequest) {
        Map<String, Object> response = new HashMap<>();
        StringBuilder q = new StringBuilder();
        q.append(" From ").append(DeliveryDetail.class.getSimpleName()).append(" e Where 1=1 ");
//                " left join " + User.class.getSimpleName() + " u on e.user.id=u.id " +
//                " left join " + Parcel.class.getSimpleName() + " s on e.parcel.id=e.id  "
//                + "

//        if (srchRequest.getContact() != null && srchRequest.getContact().getId() != null) {
//            q.append(" and e.contact.id ='").append(srchRequest.getContact().getId()).append("'");
//        }
//
//        if (srchRequest.getContactPerson() != null) {
//            q.append(" and e.contactPerson like '%").append(srchRequest.getContactPerson()).append("%'");
//        }
//
//        if (srchRequest.getContactPersonEmail() != null) {
//            q.append(" and e.contactPersonEmail like '%").append(srchRequest.getContactPersonEmail()).append("%'");
//        }
//
//        if (srchRequest.getContactPersonPhone() != null) {
//            q.append(" and e.contactPersonPhone like '%").append(srchRequest.getContactPersonPhone()).append("%'");
//        }
//
//        if (srchRequest.getStreet() != null) {
//            q.append(" and e.street like '%").append(srchRequest.getStreet()).append("%'");
//        }
//
//        if (srchRequest.getCity() != null && srchRequest.getCity().getId() != null) {
//            q.append(" and e.city.id ='").append(srchRequest.getCity().getId()).append("'");
//        }

        TypedQuery<DeliveryDetail> query = em.createQuery("Select e " + q.toString() + " order by e.id desc", DeliveryDetail.class);
        List<DeliveryDetail> res = query.setFirstResult(page).setMaxResults(rowCount).getResultList();
        response.put("items", res);
        response.put("total_count", em.createQuery("SELECT count(1) " + q.toString()).getSingleResult());
        return response;
    }
}
