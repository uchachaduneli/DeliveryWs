package ge.bestline.delivery.ws.dao;

import ge.bestline.delivery.ws.dto.CourierCheckInOutDTO;
import ge.bestline.delivery.ws.entities.CourierCheckInOut;
import ge.bestline.delivery.ws.entities.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.HashMap;
import java.util.Map;

@Repository
public class UserDao {
    EntityManager em;

    public UserDao(EntityManager em) {
        this.em = em;
    }

    public Map<String, Object> findAll(int page, int rowCount, User srchRequest) {
        Map<String, Object> response = new HashMap<>();
        StringBuilder q = new StringBuilder();
        q.append(" From ").append(User.class.getSimpleName()).append(" e JOIN e.role r Where e.deleted=2 ");

        if (srchRequest.getCity() != null && srchRequest.getCity().getId() != null) {
            q.append(" and e.city.id ='").append(srchRequest.getCity().getId()).append("'");
        }

        if (srchRequest.getName() != null) {
            q.append(" and e.name like '%").append(srchRequest.getName()).append("%'");
        }

        if (srchRequest.getParentUserId() != null) {
            q.append(" and ( e.id ='").append(srchRequest.getParentUserId())
                    .append("' or e.parentUserId='")
                    .append(srchRequest.getParentUserId()).append("') ");
        }

        if (srchRequest.getLastName() != null) {
            q.append(" and e.lastName like '%").append(srchRequest.getLastName()).append("%'");
        }

        if (srchRequest.getPersonalNumber() != null) {
            q.append(" and e.personalNumber like '%").append(srchRequest.getPersonalNumber()).append("%'");
        }

        if (srchRequest.getPhone() != null) {
            q.append(" and e.phone like '%").append(srchRequest.getPhone()).append("%'");
        }

        if (srchRequest.getRoute() != null && srchRequest.getRoute().getId() != null) {
            q.append(" and e.route.id ='").append(srchRequest.getRoute().getId()).append("'");
        }

        if (srchRequest.getSrchRoleName() != null && !srchRequest.getSrchRoleName().isEmpty()) {
            q.append(" and r.name in :rolesInList");
        }

        if (srchRequest.getUserName() != null) {
            q.append(" and e.userName='").append(srchRequest.getUserName()).append("'");
        }

        if (srchRequest.getPassword() != null) {
            q.append(" and e.password='").append(srchRequest.getPassword()).append("'");
        }

        TypedQuery<User> query = em.createQuery("SELECT DISTINCT e " + q.toString() + " order by e.id desc", User.class);
        TypedQuery<Long> cntQr = em.createQuery("SELECT count(1) " + q.toString(), Long.class);
        if (srchRequest.getSrchRoleName() != null && !srchRequest.getSrchRoleName().isEmpty()) {
            query.setParameter("rolesInList", srchRequest.getSrchRoleName());
            cntQr.setParameter("rolesInList", srchRequest.getSrchRoleName());
        }
        response.put("items", query.setFirstResult(page * rowCount).setMaxResults(rowCount).getResultList());
        response.put("total_count", cntQr.getSingleResult());
        return response;
    }

    @Transactional
    public void removeUserExistingRoles(Integer userId) {
        em.createNativeQuery("DELETE FROM user_role where user_id='" + userId + "'").executeUpdate();
    }

    public Map<String, Object> getCoutiersInOut(int page, int rowCount, CourierCheckInOutDTO srchRequest) {
        Map<String, Object> response = new HashMap<>();
        StringBuilder q = new StringBuilder(" FROM ").append(CourierCheckInOut.class);

        if (StringUtils.isNotBlank(srchRequest.getCarNumber())) {
            q.append(" and e.carNumber like '%").append(srchRequest.getCourier().getPersonalNumber()).append("%'");
        }
        if (srchRequest.isChekIn()) {
            q.append(" and e.isChekIn ='").append(srchRequest.isChekIn()).append("' ");
        }

        if (srchRequest.getCourier() != null) {
            if (StringUtils.isNotBlank(srchRequest.getCourier().getName())) {
                q.append(" and e.courier.name like '%").append(srchRequest.getCourier().getName()).append("%'");
            }
            if (StringUtils.isNotBlank(srchRequest.getCourier().getLastName())) {
                q.append(" and e.courier.lastName like '%").append(srchRequest.getCourier().getLastName()).append("%'");
            }

            if (StringUtils.isNotBlank(srchRequest.getCourier().getPersonalNumber())) {
                q.append(" and e.courier.personalNumber like '%").append(srchRequest.getCourier().getPersonalNumber()).append("%'");
            }

            if (StringUtils.isNotBlank(srchRequest.getCourier().getPhone())) {
                q.append(" and e.courier.phone like '%").append(srchRequest.getCourier().getPhone()).append("%'");
            }

            if (srchRequest.getCourier().getRoute() != null && srchRequest.getCourier().getRoute().getId() != null) {
                q.append(" and e.courier.route.id ='").append(srchRequest.getCourier().getRoute().getId()).append("'");
            }

            if (StringUtils.isNotBlank(srchRequest.getCourier().getUserName())) {
                q.append(" and e.courier.userName='").append(srchRequest.getCourier().getUserName()).append("'");
            }
        }
        TypedQuery<CourierCheckInOut> query = em.createQuery("SELECT count(1) " + q.toString(), CourierCheckInOut.class);
        TypedQuery<Long> cntQr = em.createQuery("SELECT count(1) " + q.toString(), Long.class);
        response.put("items", query.setFirstResult(page * rowCount).setMaxResults(rowCount).getResultList());
        response.put("total_count", cntQr.getSingleResult());
        return response;
    }
}
