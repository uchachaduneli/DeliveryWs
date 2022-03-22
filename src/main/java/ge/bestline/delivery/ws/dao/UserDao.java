package ge.bestline.delivery.ws.dao;

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

    public Map<String, Object> findAll(int page, int rowCount, User srchRequest) {
        Map<String, Object> response = new HashMap<>();
        StringBuilder q = new StringBuilder();
        q.append(" From ").append(User.class.getSimpleName()).append(" e JOIN e.role r Where 1=1 ");

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

        if (srchRequest.getSrchRoleName() != null && !srchRequest.getSrchRoleName().isEmpty()) {
            q.append(" and r.name in :rolesInList");
        }

        if (srchRequest.getUserName() != null) {
            q.append(" and e.userName='").append(srchRequest.getUserName()).append("'");
        }

        if (srchRequest.getPassword() != null) {
            q.append(" and e.password='").append(srchRequest.getPassword()).append("'");
        }

        TypedQuery<User> query = em.createQuery("SELECT DISTINCT e " + q.toString(), User.class);
        TypedQuery<Long> cntQr = em.createQuery("SELECT count(1) " + q.toString(), Long.class);
        if (srchRequest.getSrchRoleName() != null && !srchRequest.getSrchRoleName().isEmpty()) {
            query.setParameter("rolesInList", srchRequest.getSrchRoleName());
            cntQr.setParameter("rolesInList", srchRequest.getSrchRoleName());
        }
        response.put("items", query.setFirstResult(page).setMaxResults(rowCount).getResultList());
        response.put("total_count", cntQr.getSingleResult());
        return response;
    }

    public User findByUserNameAndPassword(String username, String password) {
        StringBuilder q = new StringBuilder()
                .append("SELECT e From ").append(User.class.getSimpleName()).append(" e Where ")
                .append(" e.userName='").append(username).append("'")
                .append(" and e.password='").append(password).append("'");
        TypedQuery<User> query = em.createQuery(q.toString(), User.class);
        return query.getSingleResult();
    }
}
