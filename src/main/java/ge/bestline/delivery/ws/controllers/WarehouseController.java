package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.entities.Warehouse;
import ge.bestline.delivery.ws.repositories.WarehouseRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/warehouse")
@CrossOrigin(origins = "http://localhost:4200")
public class WarehouseController {

    private final WarehouseRepository repo;

    public WarehouseController(WarehouseRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public Iterable<Warehouse> getAll() {
        return repo.findAll();
    }

    @GetMapping(path = "/{id}")
    public Warehouse getById(@PathVariable Integer id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID"));
    }

}
