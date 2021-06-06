package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.repositories.RoleRepository;
import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.entities.Role;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/role")
@CrossOrigin(origins = "http://localhost:4200")
public class RolesController {

    private final RoleRepository roleRepository;

    public RolesController(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @GetMapping
    public Iterable<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @GetMapping(path = "/{id}")
    public Role getById(@PathVariable Integer id) {
        return roleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can't find Record Using This ID"));
    }

}
