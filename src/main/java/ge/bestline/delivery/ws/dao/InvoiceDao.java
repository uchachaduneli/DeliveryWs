package ge.bestline.delivery.ws.dao;

import ge.bestline.delivery.ws.dto.InvoiceDTO;
import ge.bestline.delivery.ws.entities.Invoice;
import ge.bestline.delivery.ws.entities.Parcel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InvoiceDao {
    EntityManager em;

    public InvoiceDao(EntityManager em) {
        this.em = em;
    }

    public Map<String, Object> loadAllForInvoiceGeneration(int page, int rowCount) {
        Map<String, Object> response = new HashMap<>();
        List<InvoiceDTO> list =
                em.createQuery("select new ge.bestline.delivery.ws.dto.InvoiceDTO(p.payerName," +
                                " p.payerIdentNumber, count(p.payerIdentNumber)) from " + Parcel.class.getSimpleName() +
                                " p where p.deleted = 2 and p.invoiced = false group by p.payerName, p.payerIdentNumber "
                        , InvoiceDTO.class).setFirstResult(page * rowCount).setMaxResults(rowCount).getResultList();
        response.put("items", list);
        response.put("total_count", em.createNativeQuery("select count(1) from (select p.payer_ident_number from deliverydb.parcel"
                + " p where p.deleted = 2 and p.invoiced = false group by p.payer_ident_number) a").getSingleResult());
        return response;
    }

    public Map<String, Object> findAll(int page, int rowCount, InvoiceDTO srchRequest) {
        Map<String, Object> response = new HashMap<>();
        StringBuilder q = new StringBuilder();
        q.append(" From ").append(Invoice.class.getSimpleName()).append(" e Where deleted=2 ");

        if (srchRequest.getId() != null) {
            q.append(" and e.id ='").append(srchRequest.getId()).append("'");
        }

        if (StringUtils.isNotBlank(srchRequest.getName())) {
            q.append(" and e.name like '%").append(srchRequest.getName()).append("%'");
        }

        if (StringUtils.isNotBlank(srchRequest.getIdentNumber())) {
            q.append(" and e.identNumber like '%").append(srchRequest.getIdentNumber()).append("%'");
        }

        if (srchRequest.getOperationDate() != null && srchRequest.getOperationDateTo() != null) {
            q.append(" and ( e.operationDate between '").append(srchRequest.getOperationDate()).append("' and '")
                    .append(srchRequest.getOperationDateTo()).append("') ");
        } else {
            if (srchRequest.getOperationDate() != null) {
                q.append(" and e.operationDate ='").append(srchRequest.getOperationDate()).append("'");
            }
            if (srchRequest.getOperationDateTo() != null) {
                q.append(" and e.operationDate ='").append(srchRequest.getOperationDateTo()).append("'");
            }
        }

        TypedQuery<Invoice> query = em.createQuery("Select e" + q.toString(), Invoice.class);
        List<Invoice> res = query.setFirstResult(page * rowCount).setMaxResults(rowCount).getResultList();
        response.put("items", res);
        response.put("total_count", em.createQuery("SELECT count(1) " + q.toString()).getSingleResult());
        return response;
    }
}
