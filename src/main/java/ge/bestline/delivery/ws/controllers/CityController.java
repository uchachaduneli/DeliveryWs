package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.entities.City;
import ge.bestline.delivery.ws.repositories.CityRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/city")
@CrossOrigin(origins = "http://localhost:4200")
public class CityController {

    private final CityRepository cityRepository;

    public CityController(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @GetMapping
    public Iterable<City> getAllCities() {
        return cityRepository.findAll();
    }

    @GetMapping(path = "/{id}")
    public City getCitiesById(@PathVariable Integer id) {
        return cityRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID"));
    }

}
