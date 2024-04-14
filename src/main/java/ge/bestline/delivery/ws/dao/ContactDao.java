package ge.bestline.delivery.ws.dao;

import ge.bestline.delivery.ws.entities.Contact;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ContactDao {
    EntityManager em;

    public ContactDao(EntityManager em) {
        this.em = em;
    }

    public Map<String, Object> findAll(int page, int rowCount, Contact srchRequest) {
        Map<String, Object> response = new HashMap<>();
        StringBuilder q = new StringBuilder();
        q.append(" From ").append(Contact.class.getSimpleName())
                .append(" e  Where e.deleted=2 ");
//                .append(" Where 1=1  e LEFT JOIN ")
//                .append(ContactAddress.class.getSimpleName())
//                .append(" c on e.mainAddress.id=c.id Where 1=1 ");

        if (srchRequest.getId() != null) {
            q.append(" and e.id ='").append(srchRequest.getId()).append("'");
        }

        if (srchRequest.getUser() != null) {
            q.append(" and e.user.id ='").append(srchRequest.getUser().getId()).append("'");
        }

        if (!StringUtils.isBlank(srchRequest.getName())) {
            q.append(" and e.name like '%").append(srchRequest.getName()).append("%'");
        }

        if (srchRequest.getEmail() != null) {
            q.append(" and e.email like '%").append(srchRequest.getEmail()).append("%'");
        }

        if (srchRequest.getType() != null) {
            q.append(" and e.type ='").append(srchRequest.getType()).append("'");
        }

        if (srchRequest.getStatus() != null) {
            q.append(" and e.status ='").append(srchRequest.getStatus()).append("'");
        }

        if (srchRequest.getDeReGe() != null) {
            q.append(" and e.deReGe ='").append(srchRequest.getDeReGe()).append("'");
        }

        if (srchRequest.getDeleted() != null) {
            q.append(" and e.deleted ='").append(srchRequest.getDeleted()).append("'");
        }

        if (srchRequest.getHasContract() != null) {
            q.append(" and e.hasContract ='").append(srchRequest.getHasContract()).append("'");
        }

        if (!StringUtils.isBlank(srchRequest.getIdentNumber())) {
            q.append(" and e.identNumber ='").append(srchRequest.getIdentNumber()).append("'");
        }

        TypedQuery<Contact> query = em.createQuery("SELECT e " + q.toString() + " order by e.id desc", Contact.class);
        List<Contact> res = query.setFirstResult(page * rowCount).setMaxResults(rowCount).getResultList();
        response.put("items", res);
        response.put("total_count", em.createQuery("SELECT count(1) " + q.toString()).getSingleResult());


        // return main address if exact filtering field are presented
//        if (!StringUtils.isBlank(srchRequest.getIdentNumber()) || srchRequest.getId() != null && res != null && !res.isEmpty()) {
//            TypedQuery<ContactAddress> addressTypedQuery = em.createQuery("Select e from " + ContactAddress.class.getSimpleName()
//                    + " e where e.isPayAddress='1' and e.contact.id=" + res.get(0).getId(), ContactAddress.class);
//            response.put("mainAddress", query.getResultList());
//        }
        return response;
    }
}
