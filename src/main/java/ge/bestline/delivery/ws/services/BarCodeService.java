package ge.bestline.delivery.ws.services;

import ge.bestline.delivery.ws.entities.Parcel;
import ge.bestline.delivery.ws.repositories.ParcelRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Log4j2
@Service
public class BarCodeService {
    ParcelRepository repo;

    public BarCodeService(ParcelRepository repo) {
        this.repo = repo;
    }

    public List<String> getBarcodes(int count) {
        log.info("Generating Barcodes Started For Count: " + count);
        List<String> list = generateBarcodes(count);
        List<Parcel> existingBarCodes = repo.findByBarCodeIn(list);
        if (existingBarCodes.isEmpty()) {
            return list;
        } else {
            log.warn("Some Of Generated BarCodes Have Founded In Database, Generating Again For Existing Ones");
            existingBarCodes.forEach(parcel -> {
                list.remove(parcel.getBarCode());
            });
            list.addAll(generateBarcodes(existingBarCodes.size()));
        }
        return list;
    }

    private List<String> generateBarcodes(int countToGenerate) {
        List<String> res = new ArrayList<>();
        int[] result = new Random().ints(10_000_000, 100_000_000)
                .distinct().limit(countToGenerate).toArray();
        for (int i : result) {
            res.add(i + "");
        }
        return res;
    }
}
