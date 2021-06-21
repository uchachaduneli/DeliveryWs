package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.entities.Tranzit;
import ge.bestline.delivery.ws.repositories.TranzitRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/tranzit")
@CrossOrigin(origins = "http://localhost:4200")
public class TranzitController {

    private final TranzitRepository repo;

    public TranzitController(TranzitRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public Iterable<Tranzit> getAll() {
        return repo.findAll();
    }

    @GetMapping(path = "/{id}")
    public Tranzit getById(@PathVariable Integer id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID"));
    }

}
