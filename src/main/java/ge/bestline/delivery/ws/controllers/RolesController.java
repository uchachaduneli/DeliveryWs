package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.entities.Role;
import ge.bestline.delivery.ws.repositories.RoleRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping(path = "/role")
@CrossOrigin(origins = "http://localhost:4200")
public class RolesController {

    private final RoleRepository roleRepository;

    public RolesController(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @GetMapping
    public Iterable<Role> getAllRolles() {
        log.info("Getting Roles");
        return roleRepository.findAll();
    }

}
