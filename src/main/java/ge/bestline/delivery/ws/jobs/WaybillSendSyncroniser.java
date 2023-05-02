package ge.bestline.delivery.ws.jobs;

import ge.bestline.delivery.ws.services.RsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Log4j2
@Component
@EnableScheduling
@ConditionalOnProperty(havingValue = "true", name = "data.rs.syncEnabled", matchIfMissing = true)
public class WaybillSendSyncroniser {
    private final RsService rsService;

    @Value("${data.rs.syncEnabled}")
    private boolean syncEnabled;

    public WaybillSendSyncroniser(RsService rsService) {
        this.rsService = rsService;
    }

    @Scheduled(cron = "${data.rs.waybill.sendInterval}")
    public void runTask() {
        System.out.println("WaybillSendSyncroniser " + new Date().toString());
//        try {
//            if (syncEnabled) {
//                rsService.syncWayBills();
//                log.info("************ RS Waybill Sync Started ************");
//            }
//        } catch (Exception e) {
//            log.error("Waybill Sync Failed", e);
//            return;
//        }
    }
}
