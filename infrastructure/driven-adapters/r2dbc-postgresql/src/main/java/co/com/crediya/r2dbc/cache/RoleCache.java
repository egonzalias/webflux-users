package co.com.crediya.r2dbc.cache;

import co.com.crediya.model.user.Role;
import co.com.crediya.r2dbc.mapper.RoleMapper;
import co.com.crediya.r2dbc.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class RoleCache {

    private final RoleRepository repository;
    // Use ConcurrentHashMap for thread-safe access in concurrent environments (e.g., reactive applications)
    private final Map<String, Role> cache = new ConcurrentHashMap<>();
    private final RoleMapper mapper;

    @PostConstruct
    public void preloadCache() {
        repository.findAll()
                .map(mapper::toModel)
                .doOnNext(status -> cache.put(status.getName(), status))
                .subscribe();
    }

    public Mono<Role> findByName(String name) {
        Role cached = cache.get(name);
        if (cached != null) return Mono.just(cached);
        return repository.findByName(name)
                .map(mapper::toModel)
                .doOnNext(status -> cache.put(name, status));
    }
}
