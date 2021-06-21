package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.entities.Services;
import ge.bestline.delivery.ws.repositories.ServicesRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/service")
@CrossOrigin(origins = "http://localhost:4200")
public class ServiceController {

    private final ServicesRepository repo;

    public ServiceController(ServicesRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public Iterable<Services> getAll() {
        return repo.findAll();
    }

    @GetMapping(path = "/{id}")
    public Services getById(@PathVariable Integer id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID"));
    }

}
