package ge.bestline.delivery.ws.dao;

import ge.bestline.delivery.ws.entities.Zone;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ZoneDao {
    EntityManager em;

    public ZoneDao(EntityManager em) {
        this.em = em;
    }

    public Map<String, Object> findAll(int page, int rowCount, Zone srchRequest) {
        Map<String, Object> response = new HashMap<>();
        StringBuilder q = new StringBuilder();
        q.append(" From ").append(Zone.class.getSimpleName()).append(" e Where 1=1 ");

        if (srchRequest.getId() != null) {
            q.append(" and e.id ='").append(srchRequest.getId()).append("'");
        }

        if (srchRequest.getDeleted() != null) {
            q.append(" and e.deleted ='").append(srchRequest.getDeleted()).append("'");
        }

        if (srchRequest.getName() != null) {
            q.append(" and e.name like '%").append(srchRequest.getName()).append("%'");
        }

        TypedQuery<Zone> query = em.createQuery("Select e" + q.toString(), Zone.class);
        List<Zone> res = query.setFirstResult(page * rowCount).setMaxResults(rowCount).getResultList();
        response.put("items", res);
        response.put("total_count", em.createQuery("SELECT count(1) " + q.toString()).getSingleResult());
        return response;
    }
}
