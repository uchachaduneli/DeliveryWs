package ge.bestline.delivery.ws.dao;

import ge.bestline.delivery.ws.dto.WaybillDTO;
import ge.bestline.delivery.ws.entities.WayBill;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class WaybillDao {
    EntityManager em;

    public WaybillDao(EntityManager em) {
        this.em = em;
    }

    public Map<String, Object> findAll(int page, int rowCount, WaybillDTO srchRequest) {
        Map<String, Object> response = new HashMap<>();
        StringBuilder q = new StringBuilder();
        q.append(" From ").append(WayBill.class.getSimpleName()).append(" e Where 1=1 ");

        if (srchRequest.getId() != null) {
            q.append(" and e.id ='").append(srchRequest.getId()).append("'");
        }

        if (srchRequest.getRsCreateDate() != null && srchRequest.getRsCreateDateTo() != null) {
            q.append(" and ( e.rsCreateDate between '").append(srchRequest.getRsCreateDate()).append("' and '")
                    .append(srchRequest.getRsCreateDateTo()).append("') ");
        } else {
            if (srchRequest.getCreatedTime() != null) {
                q.append(" and e.rsCreateDate >'").append(srchRequest.getRsCreateDate()).append("'");
            }
            if (srchRequest.getRsCreateDateTo() != null) {
                q.append(" and e.rsCreateDate <'").append(srchRequest.getRsCreateDateTo()).append("'");
            }
        }

        if (StringUtils.isNotBlank(srchRequest.getSyncStatus())) {
            q.append(" and e.syncStatus ='").append(srchRequest.getSyncStatus()).append("'");
        }
        if (StringUtils.isNotBlank(srchRequest.getWaybillNumber())) {
            q.append(" and e.waybillNumber ='").append(srchRequest.getWaybillNumber()).append("'");
        }
        if (StringUtils.isNotBlank(srchRequest.getCarNumber())) {
            q.append(" and e.carNumber ='").append(srchRequest.getCarNumber()).append("'");
        }
        if (StringUtils.isNotBlank(srchRequest.getDriverTin())) {
            q.append(" and e.driverTin ='").append(srchRequest.getDriverTin()).append("'");
        }
        if (srchRequest.getEndAddress() != null) {
            q.append(" and e.endAddress like '%").append(srchRequest.getEndAddress()).append("%'");
        }
        if (srchRequest.getStartAddress() != null) {
            q.append(" and e.startAddress like '%").append(srchRequest.getStartAddress()).append("%'");
        }
        if (srchRequest.getWaybillComment() != null) {
            q.append(" and e.waybillComment like '%").append(srchRequest.getWaybillComment()).append("%'");
        }
        if (srchRequest.getDriverName() != null) {
            q.append(" and e.driverName like '%").append(srchRequest.getDriverName()).append("%'");
        }
        if (StringUtils.isNotBlank(srchRequest.getBuyerTin())) {
            q.append(" and e.buyerTin ='").append(srchRequest.getBuyerTin()).append("'");
        }

        if (srchRequest.getBuyerName() != null) {
            q.append(" and e.buyerName like '%").append(srchRequest.getBuyerName()).append("%'");
        }

        TypedQuery<WayBill> query = em.createQuery("Select e" + q.toString() + " order by e.rsCreateDate desc", WayBill.class);
        List<WayBill> res = query.setFirstResult(page * rowCount).setMaxResults(rowCount).getResultList();
        response.put("items", res);
        response.put("total_count", em.createQuery("SELECT count(1) " + q.toString()).getSingleResult());
        return response;
    }
}
