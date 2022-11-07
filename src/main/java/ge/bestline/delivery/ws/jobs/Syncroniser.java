package ge.bestline.delivery.ws.jobs;

import ge.bestline.delivery.ws.services.RsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${data.rs.syncEnabled}")
    private boolean syncEnabled;

    public Syncroniser(RsService rsService) {
        this.rsService = rsService;
    }

    @Scheduled(cron = "${data.rs.syncInterval}")
    public void runTask() {
        try {
            if (syncEnabled) {
                rsService.syncWayBills();
                log.info("************ RS Waybill Sync Started ************");
            }
        } catch (Exception e) {
            log.error("Waybill Sync Failed", e);
            return;
        }
    }
}
