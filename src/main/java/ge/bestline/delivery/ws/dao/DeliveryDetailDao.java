package ge.bestline.delivery.ws.dao;

import ge.bestline.delivery.ws.dto.DeliveryDetailDTO;
import ge.bestline.delivery.ws.entities.DeliveryDetail;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DeliveryDetailDao {
    EntityManager em;

    public DeliveryDetailDao(EntityManager em) {
        this.em = em;
    }

    public Map<String, Object> findAll(int page, int rowCount, DeliveryDetailDTO srchRequest) {
        Map<String, Object> response = new HashMap<>();
        StringBuilder q = new StringBuilder();
        q.append(" From delivery_detail_parcels ddp join delivery_detail e on ddp.delivery_detail_id = e.id Where 1=1 ");

        if (StringUtils.isNotBlank(srchRequest.getDetailBarCode())) {
            q.append(" and e.detail_bar_code ='").append(srchRequest.getDetailBarCode()).append("'");
        }
        if (StringUtils.isNotBlank(srchRequest.getParcelBarCode())) {
            q.append(" and (select p.bar_code from parcel p where p.id = ddp.parcels_id) ='").append(srchRequest.getParcelBarCode()).append("'");
        }

        if (srchRequest.getUserId() != null) {
            q.append(" and e.user_id =").append(srchRequest.getUserId());
        }

        if (srchRequest.getRouteId() != null) {
            q.append(" and e.route_id =").append(srchRequest.getUserId());
        }
        if (srchRequest.getWarehouseId() != null) {
            q.append(" and e.warehouse_id =").append(srchRequest.getWarehouseId());
        }

        if (srchRequest.getCreatedTime() != null && srchRequest.getCreatedTimeTo() != null) {
            q.append(" and ( e.created_time between '").append(srchRequest.getCreatedTime()).append("' and '")
                    .append(srchRequest.getCreatedTimeTo()).append("') ");
        } else {
            if (srchRequest.getCreatedTime() != null) {
                q.append(" and e.created_time >'").append(srchRequest.getCreatedTime()).append("'");
            }
            if (srchRequest.getCreatedTimeTo() != null) {
                q.append(" and e.created_time <'").append(srchRequest.getCreatedTimeTo()).append("'");
            }
        }

        Query query = em.createNativeQuery("Select distinct e.* " + q.toString() + " order by e.id desc", DeliveryDetail.class);
        List<DeliveryDetail> res = query.setFirstResult(page * rowCount).setMaxResults(rowCount).getResultList();
        response.put("items", res);
        response.put("total_count", em.createNativeQuery("SELECT count(distinct e.id) " + q.toString()).getSingleResult());
        return response;
    }
}
