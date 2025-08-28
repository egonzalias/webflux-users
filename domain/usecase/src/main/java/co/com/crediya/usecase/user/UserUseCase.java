package co.com.crediya.usecase.user;

import co.com.crediya.model.exception.ValidationException;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.LoggerService;
import co.com.crediya.model.user.gateways.PasswordService;
import co.com.crediya.model.user.gateways.RoleRepository;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository repository;
    private final RoleRepository roleRepository;
    private final PasswordService passwordService;
    private final LoggerService logger;

    public Mono<Void> registerUser(User user){
        //The block inside defer is executed only if it is really needed, i.e. when findByEmail returned empty
        return roleRepository.findRoleByName(user.getRole())
                .switchIfEmpty(Mono.defer(()-> {
                    logger.info("Registro fallido: el rol '{}' no es válido", user.getRole());
                    return Mono.error(new ValidationException(
                            List.of("El rol '" + user.getRole() + "' es incorrecto o no existe en la base de datos.")));
                }))
                .then(repository.findByEmail(user.getEmail()))
                .flatMap(existingUser ->{
                    logger.info("Registro fallido: el correo '{}' ya está registrado", user.getEmail());
                    return Mono.error(new ValidationException(
                            List.of("El correo electrónico ya está registrado")
                    ));
                })
                .switchIfEmpty(
                        Mono.defer(() -> {
                            user.setPassword(passwordService.encode(user.getPassword()));
                            return repository.registerUser(user);
                        })
                ).then();
    }

    public Mono<User> loginUser(String email){
        return repository.findByEmail(email)
                .map(user -> User.builder()
                        .email(user.getEmail())
                        .password(user.getPassword())
                        .role(user.getRole())
                        .build());
    }

}
