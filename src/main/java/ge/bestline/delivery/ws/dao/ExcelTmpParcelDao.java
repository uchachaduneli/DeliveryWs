package ge.bestline.delivery.ws.dao;

import ge.bestline.delivery.ws.entities.ExcelTmpParcel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ExcelTmpParcelDao {
    EntityManager em;

    public ExcelTmpParcelDao(EntityManager em) {
        this.em = em;
    }

    public Map<String, Object> findAll(int page, int rowCount, ExcelTmpParcel srchRequest) {
        Map<String, Object> response = new HashMap<>();
        StringBuilder q = new StringBuilder();
        q.append(ExcelTmpParcel.class.getSimpleName()).append(" e Where 1=1 ");

        if (srchRequest.getTmpIdForPerExcel() != null) {
            q.append(" and e.tmpIdForPerExcel ='").append(srchRequest.getTmpIdForPerExcel()).append("'");
        }
        if (srchRequest.getAuthor() != null) {
            q.append(" and e.author.id =").append(srchRequest.getAuthor().getId());
        }
        if (srchRequest.getSender() != null) {
            q.append(" and e.sender.id =").append(srchRequest.getSender().getId());
        }
        if (srchRequest.getRoute() != null) {
            q.append(" and e.route.id =").append(srchRequest.getRoute().getId());
        }

        TypedQuery<ExcelTmpParcel> query = em.createQuery("SELECT e FROM " + q.toString(), ExcelTmpParcel.class);
        List<ExcelTmpParcel> res = query.setFirstResult(page * rowCount).setMaxResults(rowCount).getResultList();
        response.put("items", res);
        response.put("total_count", em.createQuery("SELECT count(1) FROM "+ q.toString()).getSingleResult());
        return response;
    }
}
