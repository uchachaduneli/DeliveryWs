package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.entities.Zone;
import ge.bestline.delivery.ws.repositories.ZoneRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/zone")
@CrossOrigin(origins = "http://localhost:4200")
public class ZoneController {

    private final ZoneRepository zoneRepository;

    public ZoneController(ZoneRepository zoneRepository) {
        this.zoneRepository = zoneRepository;
    }

    @GetMapping
    public Iterable<Zone> getAllZones() {
        return zoneRepository.findAll();
    }

    @GetMapping(path = "/{id}")
    public Zone getZonesById(@PathVariable Integer id) {
        return zoneRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID"));
    }

}
