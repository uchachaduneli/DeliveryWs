package ge.bestline.delivery.ws.dao;

import ge.bestline.delivery.ws.entities.WayBill;
import ge.bestline.delivery.ws.entities.Zone;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class RsDao {
    EntityManager em;

    public RsDao(EntityManager em) {
        this.em = em;
    }

    public Map<String, Object> findAll(int page, int rowCount, WayBill srchRequest) {
        Map<String, Object> response = new HashMap<>();
        StringBuilder q = new StringBuilder();
        q.append(" From ").append(WayBill.class.getSimpleName()).append(" e Where 1=1 ");

        if (srchRequest.getId() != null) {
            q.append(" and e.id ='").append(srchRequest.getId()).append("'");
        }

        if (srchRequest.getBuyerTin() != null) {
            q.append(" and e.buyerTin ='").append(srchRequest.getBuyerTin()).append("'");
        }

        if (srchRequest.getBuyerName() != null) {
            q.append(" and e.buyerName like '%").append(srchRequest.getBuyerName()).append("%'");
        }

        TypedQuery<WayBill> query = em.createQuery("Select e" + q.toString(), WayBill.class);
        List<WayBill> res = query.setFirstResult(page).setMaxResults(rowCount).getResultList();
        response.put("items", res);
        response.put("total_count", em.createQuery("SELECT count(1) " + q.toString()).getSingleResult());
        return response;
    }
}
