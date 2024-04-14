package ge.bestline.delivery.ws.dao;

import ge.bestline.delivery.ws.dto.CourierCheckInOutDTO;
import ge.bestline.delivery.ws.entities.CourierCheckInOut;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ChekInOutDao {
    EntityManager em;

    public ChekInOutDao(EntityManager em) {
        this.em = em;
    }

    public Map<String, Object> findAll(int page, int rowCount, CourierCheckInOutDTO srchRequest) {
        Map<String, Object> response = new HashMap<>();
        StringBuilder q = new StringBuilder();
        q.append(" From ").append(CourierCheckInOut.class.getSimpleName()).append(" e Where 1=1 ");

        if (srchRequest.getId() != null) {
            q.append(" and e.id ='").append(srchRequest.getId()).append("'");
        }

        if (srchRequest.getCreatedTime() != null && srchRequest.getCreateTimeTo() != null) {
            q.append(" and ( e.createdTime between '").append(srchRequest.getCreatedTime()).append("' and '")
                    .append(srchRequest.getCreateTimeTo()).append("') ");
        } else {
            if (srchRequest.getCreatedTime() != null) {
                q.append(" and e.createdTime >'").append(srchRequest.getCreatedTime()).append("'");
            }
            if (srchRequest.getStrCreatedTimeTo() != null) {
                q.append(" and e.createdTime <'").append(srchRequest.getCreatedTime()).append("'");
            }
        }

        if (srchRequest.getOperationTime() != null && srchRequest.getOperationTimeTo() != null) {
            q.append(" and ( e.operationTime between '").append(srchRequest.getOperationTime()).append("' and '")
                    .append(srchRequest.getOperationTimeTo()).append("') ");
        } else {
            if (srchRequest.getOperationTime() != null) {
                q.append(" and e.operationTime >'").append(srchRequest.getOperationTime()).append("'");
            }
            if (srchRequest.getOperationTimeTo() != null) {
                q.append(" and e.operationTime <'").append(srchRequest.getOperationTimeTo()).append("'");
            }
        }

        if (StringUtils.isNotBlank(srchRequest.getCarNumber())) {
            q.append(" and e.carNumber ='").append(srchRequest.getCarNumber()).append("'");
        }
        if (srchRequest.getIsCheckinParam() != null) {
            q.append(" and e.isChekIn ='").append(srchRequest.isChekIn()).append("'");
        }

        if (srchRequest.getOdometer() != null && srchRequest.getOdometerTo() != null) {
            q.append(" and e.odometer >'").append(srchRequest.getOdometer()).append("' and e.odometer <'").append(srchRequest.getOdometerTo()).append("'");
        } else {
            if (srchRequest.getOdometer() != null) {
                q.append(" and e.odometer >'").append(srchRequest.getOdometer()).append("'");
            }
            if (srchRequest.getOdometerTo() != null) {
                q.append(" and e.odometer <'").append(srchRequest.getOdometerTo()).append("'");
            }
        }

        TypedQuery<CourierCheckInOut> query = em.createQuery("Select e" + q.toString() + " order by e.rsCreateDate desc", CourierCheckInOut.class);
        List<CourierCheckInOut> res = query.setFirstResult(page * rowCount).setMaxResults(rowCount).getResultList();
        response.put("items", res);
        response.put("total_count", em.createQuery("SELECT count(1) " + q.toString()).getSingleResult());
        return response;
    }
}
