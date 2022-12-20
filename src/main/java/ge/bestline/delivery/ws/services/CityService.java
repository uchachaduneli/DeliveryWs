package ge.bestline.delivery.ws.services;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.entities.City;
import ge.bestline.delivery.ws.repositories.CityRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class CityService {
    private final CityRepository repo;

    public CityService(CityRepository repo) {
        this.repo = repo;
    }

    public City getLongestDestZone(Integer cityOneId, Integer cityTwoId) {
        City city1 = repo.findByIdAndDeleted(cityOneId, 2).orElseThrow(() -> new ResourceNotFoundException("Can't find City Record Using This ID " + cityOneId));
        City city2 = repo.findByIdAndDeleted(cityTwoId, 2).orElseThrow(() -> new ResourceNotFoundException("Can't find City Record Using This ID " + cityTwoId));
        if (city1.getZone().getName() == city2.getZone().getName()) {
            return city1;
        }
        return (city2.getZone().getName() > city1.getZone().getName() ? city2 : city1);
    }
}
