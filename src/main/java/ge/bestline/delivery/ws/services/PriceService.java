package ge.bestline.delivery.ws.services;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.entities.TariffDetail;
import ge.bestline.delivery.ws.repositories.TariffDetailsRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
public class PriceService {
    private final TariffDetailsRepository repoDetails;

    public PriceService(TariffDetailsRepository repoDetails) {
        this.repoDetails = repoDetails;
    }

    public Double calculatePrice(
            Integer serviceId,
            Integer tariffId,
            Integer zoneId,
            Double weight) {
        // try to find price with exact weight
        List<TariffDetail> details = repoDetails.findByDeletedAndService_IdAndTariff_IdAndZone_IdAndWeight(2, serviceId, tariffId, zoneId, weight);
        if (details.isEmpty()) {
            //  price with exact weight not fount trying to find first price with greater weight
            details = repoDetails.findByDeletedAndService_IdAndTariff_IdAndZone_IdAndWeightGreaterThanOrderByWeightAsc(2, serviceId, tariffId, zoneId, weight);
            if (details.isEmpty()) {
                throw new ResourceNotFoundException("Can't get Price from TariffDetails Using This IDes {serviceId}/{tariffId}/{zoneId}/{weight} : "
                        + serviceId + tariffId + "/" + zoneId + "/" + weight);
            }
            return details.get(0).getPrice();
        } else {
            return details.get(0).getPrice();
        }
    }
}
