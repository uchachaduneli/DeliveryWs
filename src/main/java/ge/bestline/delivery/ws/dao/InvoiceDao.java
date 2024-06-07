package ge.bestline.delivery.ws.dao;

import ge.bestline.delivery.ws.dto.InvoiceDTO;
import ge.bestline.delivery.ws.entities.Invoice;
import ge.bestline.delivery.ws.entities.Parcel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InvoiceDao {
    EntityManager em;

    public InvoiceDao(EntityManager em) {
        this.em = em;
    }

    public Map<String, Object> loadAllForInvoiceGeneration(int page, int rowCount, InvoiceDTO srchObj) {
        Map<String, Object> response = new HashMap<>();
        String qrStr = "";
        String nativeQrStr = "";
        if (StringUtils.isNotBlank(srchObj.getName())) {
            qrStr += " and p.payerName like '" + srchObj.getName() + "%'";
            nativeQrStr += " and p.payer_name like '" + srchObj.getName() + "%'";
        }
        if (StringUtils.isNotBlank(srchObj.getIdentNumber())) {
            qrStr += " and p.payerIdentNumber like '" + srchObj.getIdentNumber() + "%' ";
            nativeQrStr += " and p.payer_ident_number like '" + srchObj.getIdentNumber() + "%' ";
        }
        List<InvoiceDTO> list =
                em.createQuery("select new ge.bestline.delivery.ws.dto.InvoiceDTO(" +
                                " p.payerIdentNumber, count(p.payerIdentNumber), sum(p.totalPrice)) from " + Parcel.class.getSimpleName() +
                                " p where p.deleted = 2 and p.invoiced = false and p.paymentType=1 and p.payerIdentNumber is not null " + qrStr +
                                " group by p.payerIdentNumber "
                        , InvoiceDTO.class).setFirstResult(page * rowCount).setMaxResults(rowCount).getResultList();
        for (InvoiceDTO invoiceDTO : list) {
            List<String> names = em.createQuery("select e.payerName from " + Parcel.class.getSimpleName()
                    + " e where e.payerIdentNumber='" + invoiceDTO.getIdentNumber() + "'", String.class).setMaxResults(1).getResultList();
            invoiceDTO.setName(names != null && !names.isEmpty() ? names.get(0) : "-");
        }
        response.put("items", list);
        response.put("total_count", em.createNativeQuery("select count(1) from (select p.payer_ident_number from deliverydb.parcel"
                + " p where p.deleted = 2 and p.invoiced = false and p.payment_type=1 and p.payer_ident_number is not null " + nativeQrStr +
                "group by p.payer_ident_number) a").getSingleResult());
        return response;
    }

    public Map<String, Object> findAll(int page, int rowCount, InvoiceDTO srchRequest) {
        Map<String, Object> response = new HashMap<>();
        StringBuilder q = new StringBuilder();
        q.append(" From ").append(Invoice.class.getSimpleName()).append(" e Where 1=1 ");

        if (srchRequest.getId() != null) {
            q.append(" and e.id =").append(srchRequest.getId());
        }

        if (srchRequest.getAuthor() != null && srchRequest.getAuthor().getId() > 0) {
            q.append(" and e.author.id =").append(srchRequest.getAuthor().getId());
        }

        if (StringUtils.isNotBlank(srchRequest.getName())) {
            q.append(" and e.name like '%").append(srchRequest.getName()).append("%'");
        }

        if (StringUtils.isNotBlank(srchRequest.getStatus())) {
            q.append(" and e.status ='").append(srchRequest.getStatus()).append("'");
        }
        if (StringUtils.isNotBlank(srchRequest.getPayStatus())) {
            q.append(" and e.payStatus ='").append(srchRequest.getPayStatus()).append("'");
        }

        if (StringUtils.isNotBlank(srchRequest.getIdentNumber())) {
            q.append(" and e.identNumber = '").append(srchRequest.getIdentNumber()).append("'");
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

        TypedQuery<Invoice> query = em.createQuery("Select e " + q.toString() + " order by e.id desc", Invoice.class);
        List<Invoice> res = query.setFirstResult(page * rowCount).setMaxResults(rowCount).getResultList();
        response.put("items", res);
        response.put("total_count", em.createQuery("SELECT count(1) " + q.toString()).getSingleResult());
        return response;
    }
}
