package ge.bestline.delivery.ws.dao;

import ge.bestline.delivery.ws.entities.ContactAddress;
import ge.bestline.delivery.ws.entities.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserDao {
    EntityManager em;

    public UserDao(EntityManager em) {
        this.em = em;
    }

    public Map<String, Object> findAll(User srchRequest) {
        Map<String, Object> response = new HashMap<>();
        StringBuilder q = new StringBuilder();
        q.append("SELECT DISTINCT e From ").append(User.class.getSimpleName()).append(" e JOIN e.role r Where 1=1 ");

        if (srchRequest.getCity() != null && srchRequest.getCity().getId() != null) {
            q.append(" and e.city.id ='").append(srchRequest.getCity().getId()).append("'");
        }

        if (srchRequest.getName() != null) {
            q.append(" and e.name like '%").append(srchRequest.getName()).append("%'");
        }

        if (srchRequest.getLastName() != null) {
            q.append(" and e.lastName like '%").append(srchRequest.getLastName()).append("%'");
        }

        if (srchRequest.getPersonalNumber() != null) {
            q.append(" and e.personalNumber like '%").append(srchRequest.getPersonalNumber()).append("%'");
        }

        if (srchRequest.getPhone() != null) {
            q.append(" and e.phone like '%").append(srchRequest.getPhone()).append("%'");
        }

        if (srchRequest.getRoute() != null && srchRequest.getRoute().getId() != null) {
            q.append(" and e.route.id ='").append(srchRequest.getRoute().getId()).append("'");
        }

        if (srchRequest.getSrchRoleName() != null) {
            q.append(" and r.name='").append(srchRequest.getSrchRoleName()).append("'");
        }

        TypedQuery<User> query = em.createQuery(q.toString(), User.class);
        List<User> res = query.getResultList();
        response.put("items", res);
        response.put("total_count", res.size());
        return response;
    }
}
