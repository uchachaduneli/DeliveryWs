package ge.bestline.delivery.ws.dao;

import ge.bestline.delivery.ws.entities.City;
import ge.bestline.delivery.ws.entities.Contact;
import ge.bestline.delivery.ws.entities.ContactAddress;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ContactAddressDao {
    EntityManager em;

    public ContactAddressDao(EntityManager em) {
        this.em = em;
    }

    public Map<String, Object> findAll(int page, int rowCount, ContactAddress srchRequest) {
        Map<String, Object> response = new HashMap<>();
        StringBuilder q = new StringBuilder();
        q.append(" From ").append(ContactAddress.class.getSimpleName()).append(" e" +
                " left join " + Contact.class.getSimpleName() + " c on e.contact.id=c.id " +
                " left join " + City.class.getSimpleName() + " s on e.city.id=s.id  Where 1=1 ");
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

        if (srchRequest.getCity() != null && srchRequest.getCity().getId() != null) {
            q.append(" and e.city.id ='").append(srchRequest.getCity().getId()).append("'");
        }

        TypedQuery<ContactAddress> query = em.createQuery("Select e " + q.toString() + " order by e.isPayAddress asc, e.id desc", ContactAddress.class);
        List<ContactAddress> res = query.setFirstResult(page * rowCount).setMaxResults(rowCount).getResultList();
        response.put("items", res);
        response.put("total_count", em.createQuery("SELECT count(1) " + q.toString()).getSingleResult());
        return response;
    }
}
