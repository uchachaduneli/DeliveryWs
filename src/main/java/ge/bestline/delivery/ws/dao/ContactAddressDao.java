package ge.bestline.delivery.ws.dao;

import ge.bestline.delivery.ws.entities.ContactAddress;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ContactAddressDao {
    EntityManager em;

    public ContactAddressDao(EntityManager em) {
        this.em = em;
    }

    public Map<String, Object> findAll(ContactAddress srchRequest) {
        Map<String, Object> response = new HashMap<>();
        StringBuilder q = new StringBuilder();
        q.append("Select e From ").append(ContactAddress.class.getSimpleName()).append(" e Where 1=1 ");
        if (srchRequest.getContact() != null && srchRequest.getContact().getId() != null) {
            q.append(" and e.contact.id ='").append(srchRequest.getContact().getId()).append("'");
        }

        if (srchRequest.getContactPerson() != null) {
            q.append(" and e.contactPerson like '%").append(srchRequest.getContactPerson()).append("%'");
        }

        if (srchRequest.getContactPersonEmail() != null) {
            q.append(" and e.contactPersonEmail like '%").append(srchRequest.getContactPersonEmail()).append("%'");
        }

        if (srchRequest.getContactPersonPhone() != null) {
            q.append(" and e.contactPersonPhone like '%").append(srchRequest.getContactPersonPhone()).append("%'");
        }

        if (srchRequest.getStreet() != null) {
            q.append(" and e.street like '%").append(srchRequest.getStreet()).append("%'");
        }

        TypedQuery<ContactAddress> query = em.createQuery(q.toString(), ContactAddress.class);
        List<ContactAddress> res = query.getResultList();
        response.put("items", res);
        response.put("total_count", res.size());
        return response;
    }
}
