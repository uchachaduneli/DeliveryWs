package ge.bestline.delivery.ws.dao;

import ge.bestline.delivery.ws.entities.Parcel;
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

    public Map<String, Object> findAll(int page, int rowCount, Parcel srchRequest) {
        Map<String, Object> response = new HashMap<>();
        StringBuilder q = new StringBuilder();
        q.append(" From ").append(Parcel.class.getSimpleName()).append(" e Where e.deleted=2 ");

        if (srchRequest.getStatus() != null) {
            q.append(" and e.status.id ='").append(srchRequest.getStatus().getId()).append("'");
        }

        if (srchRequest.getCourier() != null) {
            q.append(" and e.courier.id ='").append(srchRequest.getCourier().getId()).append("'");
        }

        if (srchRequest.getAuthor() != null) {
            q.append(" and e.author.id ='").append(srchRequest.getAuthor().getId()).append("'");
        }
//
//        if (srchRequest.getDeleted() != null) {
//            q.append(" and e.deleted ='").append(srchRequest.getDeleted()).append("'");
//        }
//
//        if (srchRequest.getName() != null) {
//            q.append(" and e.name like '%").append(srchRequest.getName()).append("%'");
//        }

        TypedQuery<Parcel> query = em.createQuery("SELECT e " + q.toString() + " order by e.id desc", Parcel.class);
        TypedQuery<Long> cntQr = em.createQuery("SELECT count(1) " + q.toString(), Long.class);
        response.put("items", query.setFirstResult(page).setMaxResults(rowCount).getResultList());
        response.put("total_count", cntQr.getSingleResult());
        return response;
    }
}
