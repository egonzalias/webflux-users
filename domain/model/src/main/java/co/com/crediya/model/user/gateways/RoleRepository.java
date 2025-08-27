package co.com.crediya.model.user.gateways;

import co.com.crediya.model.user.Role;
import reactor.core.publisher.Mono;

public interface RoleRepository {
    Mono<Role> findRoleByName(String name);
}
