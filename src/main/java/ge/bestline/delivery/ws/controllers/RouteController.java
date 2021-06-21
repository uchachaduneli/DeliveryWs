package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.entities.Route;
import ge.bestline.delivery.ws.repositories.RouteRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/route")
@CrossOrigin(origins = "http://localhost:4200")
public class RouteController {

    private final RouteRepository routeRepository;

    public RouteController(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    @GetMapping
    public Iterable<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    @GetMapping(path = "/{id}")
    public Route getRoutesById(@PathVariable Integer id) {
        return routeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID"));
    }

}
