package co.com.crediya.usecase.user;

import co.com.crediya.model.exception.ValidationException;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.*;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository repository;
    private final RoleRepository roleRepository;
    private final PasswordService passwordService;
    private final TokenService tokenService;
    private final LoggerService logger;

    public Mono<Void> registerUser(User user){
        //The block inside defer is executed only if it is really needed, i.e. when findByEmail returned empty
        return roleRepository.findRoleByName(user.getRole())
                .switchIfEmpty(Mono.defer(() -> {
                    logger.info("Registro fallido: el rol '{}' no es válido", user.getRole());
                    return Mono.error(new ValidationException(
                            List.of("El rol '" + user.getRole() + "' es incorrecto o no existe en la base de datos.")
                    ));
                }))
                .flatMap(role ->
                        repository.findByEmail(user.getEmail())
                                .flatMap(existingUser -> {
                                    logger.info("Registro fallido: el correo '{}' ya está registrado", user.getEmail());
                                    return Mono.error(new ValidationException(
                                            List.of("El correo electrónico ya está registrado")
                                    ));
                                })
                                .switchIfEmpty(
                                        repository.findByDocumentNumber(user.getDocument_number())
                                                .flatMap(existingUser -> {
                                                    logger.info("Registro fallido: el documento '{}' ya está registrado", user.getDocument_number());
                                                    return Mono.error(new ValidationException(
                                                            List.of("El documento " + user.getDocument_number() + " ya está registrado.")
                                                    ));
                                                })
                                                .switchIfEmpty(
                                                        Mono.defer(() -> {
                                                            user.setPassword(passwordService.encode(user.getPassword()));
                                                            return repository.registerUser(user);
                                                        })
                                                )
                                )
                )
                .then();
    }


    public Mono<User> authenticateUser(User authUser){
        return repository.findByEmail(authUser.getEmail())
                .switchIfEmpty(Mono.defer(() -> {
                    logger.info("Autenticacion fallida: el correo '{}' no existe en la base de datos.", authUser.getEmail());
                    return Mono.error(new ValidationException(
                            List.of("El correo electrónico no esta registrado.")
                    ));
                }))
                .flatMap(user -> {
                    if(passwordService.matches(authUser.getPassword(), user.getPassword())){
                        String jwtToken = tokenService.generateToken(user);
                        user.setJwtToken(jwtToken);
                        return Mono.just(user);
                    } else {
                        return Mono.error(new ValidationException(
                                List.of("La contraseña es incorrecta.")
                        ));
                    }
                })
                .map(user -> User.builder()
                        .jwtToken(user.getJwtToken())
                        .build());
    }

}
