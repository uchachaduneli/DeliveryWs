package ge.bestline.delivery.ws.jobs;

import ge.bestline.delivery.ws.services.RsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@EnableScheduling
@ConditionalOnProperty(havingValue = "true", name = "data.rs.syncEnabled", matchIfMissing = true)
public class Syncroniser {
    private final RsService rsService;

    public Syncroniser(RsService rsService) {
        this.rsService = rsService;
    }

    @Scheduled(cron = "${data.rs.syncInterval}")
    public void runTask() {
        log.info("************ Get Transporter Waybill Sync Started ************");
        try {
            rsService.syncWayBills();
        } catch (Exception e) {
            log.error("Waybill Sync Failed", e);
            return;
        }
        log.info("************ Waybill Sync Finished ************");
    }
}
