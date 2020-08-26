package com.dev.tracker.service;

import com.dev.tracker.model.Role;

public interface RoleService {

    Role save(Role role);

    Role findRoleByName(Role.RoleName roleName);
}
