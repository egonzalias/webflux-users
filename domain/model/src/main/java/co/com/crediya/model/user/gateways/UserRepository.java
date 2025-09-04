package co.com.crediya.model.user.gateways;

import co.com.crediya.model.user.User;
import reactor.core.publisher.Mono;

public interface UserRepository {
    Mono<Void> registerUser(User user);
    Mono<User> findByEmail(String email);
    Mono<User> findByDocumentNumber(String document);
}
