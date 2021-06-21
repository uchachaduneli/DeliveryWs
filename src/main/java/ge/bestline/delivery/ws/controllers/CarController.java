package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.entities.Car;
import ge.bestline.delivery.ws.repositories.CarRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/car")
@CrossOrigin(origins = "http://localhost:4200")
public class CarController {

    private final CarRepository repo;

    public CarController(CarRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public Iterable<Car> getAll() {
        return repo.findAll();
    }

    @GetMapping(path = "/{id}")
    public Car getById(@PathVariable Integer id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID"));
    }

}
