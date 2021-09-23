package ge.bestline.delivery.ws.dao;

import ge.bestline.delivery.ws.entities.Contact;
import ge.bestline.delivery.ws.entities.ContactAddress;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
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
        q.append("Select e From ").append(Contact.class.getSimpleName()).append(" e Where 1=1 ");

        if (srchRequest.getId() != null) {
            q.append(" and e.id ='").append(srchRequest.getId()).append("'");
        }

        if (srchRequest.getName() != null) {
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

        if (srchRequest.getIdentNumber() != null) {
            q.append(" and e.identNumber ='").append(srchRequest.getIdentNumber()).append("'");
        }

        TypedQuery<Contact> query = em.createQuery(q.toString(), Contact.class);
        List<Contact> res = query.setFirstResult(page).setMaxResults(rowCount).getResultList();
        response.put("items", res);
        response.put("total_count", res.size());
        return response;
    }
}
