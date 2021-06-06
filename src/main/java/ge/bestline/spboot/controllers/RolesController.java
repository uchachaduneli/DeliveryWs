package ge.bestline.spboot.controllers;

import ge.bestline.spboot.Exception.ResourceNotFoundException;
import ge.bestline.spboot.entities.Role;
import ge.bestline.spboot.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
