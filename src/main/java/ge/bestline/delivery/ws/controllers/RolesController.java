package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.entities.Role;
import ge.bestline.delivery.ws.repositories.RoleRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping(path = "/role")
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
