package co.com.crediya.r2dbc.adapter.impl;

import co.com.crediya.model.user.Role;
import co.com.crediya.model.user.gateways.RoleRepository;
import co.com.crediya.r2dbc.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class RoleAdapter implements RoleRepository {

    private final RoleService roleService;

    @Override
    public Mono<Role> findRoleByName(String name) {
        return roleService.getRoleByName(name);
    }
}
