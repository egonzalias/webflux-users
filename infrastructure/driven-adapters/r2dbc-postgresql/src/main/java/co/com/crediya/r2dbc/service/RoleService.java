package co.com.crediya.r2dbc.service;

import co.com.crediya.model.user.Role;
import co.com.crediya.r2dbc.cache.RoleCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleCache cache;

    public Mono<Role> getRoleByName(String name) {
        return cache.findByName(name);
    }
}
