package com.agh.cinehub_backend.configurator;

import com.agh.cinehub_backend.model.Role;
import com.agh.cinehub_backend.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoleConfigurator {
    private final RoleRepository roleRepository;

    public RoleConfigurator(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    public void init() {
        if (roleRepository.count() == 0) {
            createRole("USER");
            createRole("ADMIN");
            createRole("EMPLOYEE");
        }
    }

    private void createRole(String roleName) {
        Role role = new Role();
        role.setName(roleName);
        roleRepository.save(role);
    }
}
