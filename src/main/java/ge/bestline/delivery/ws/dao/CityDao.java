package ge.bestline.delivery.ws.dao;

import ge.bestline.delivery.ws.entities.City;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CityDao {
    EntityManager em;

    public CityDao(EntityManager em) {
        this.em = em;
    }

    public Map<String, Object> findAll(int page, int rowCount, City srchRequest) {
        Map<String, Object> response = new HashMap<>();
        StringBuilder q = new StringBuilder();
        q.append(" From ").append(City.class.getSimpleName()).append(" e Where deleted=2 ");

        if (srchRequest.getId() != null) {
            q.append(" and e.id =").append(srchRequest.getId());
        }
        if (srchRequest.getZone() != null) {
            q.append(" and e.zone.id =").append(srchRequest.getZone().getId());
        }

        if (StringUtils.isNotBlank(srchRequest.getName())) {
            q.append(" and e.name like '%").append(srchRequest.getName()).append("%'");
        }

        if (StringUtils.isNotBlank(srchRequest.getCode())) {
            q.append(" and e.code like '%").append(srchRequest.getCode()).append("%'");
        }

        TypedQuery<City> query = em.createQuery("Select e" + q.toString(), City.class);
        List<City> res = query.setFirstResult(page * rowCount).setMaxResults(rowCount).getResultList();
        response.put("items", res);
        response.put("total_count", em.createQuery("SELECT count(1) " + q.toString()).getSingleResult());
        return response;
    }
}
