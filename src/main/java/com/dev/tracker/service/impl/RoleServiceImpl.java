package com.dev.tracker.service.impl;

import com.dev.tracker.model.Role;
import com.dev.tracker.repository.RoleRepository;
import com.dev.tracker.service.RoleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    public Role save(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public Role findRoleByName(Role.RoleName roleName) {
        return roleRepository.findByRoleName(roleName);
    }
}
