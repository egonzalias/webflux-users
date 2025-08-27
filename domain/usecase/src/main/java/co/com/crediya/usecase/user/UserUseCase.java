package co.com.crediya.usecase.user;

import co.com.crediya.model.exception.ValidationException;
import co.com.crediya.model.user.Role;
import co.com.crediya.model.user.User;
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

    public Mono<Void> registerUser(User user){
        return roleRepository.findRoleByName(user.getRole())
                .switchIfEmpty(Mono.error(new ValidationException(
                        List.of("El rol '" + user.getRole() + "' es incorrecto o no existe en la base de datos.")
                )))
                .then(repository.findByEmail(user.getEmail()))
                .flatMap(emailExists -> {
                    if (emailExists) {
                        return Mono.error(new ValidationException(
                                List.of("El correo electrónico ya está registrado")
                        ));
                    }

                    user.setPassword(passwordService.encode(user.getPassword()));
                    return repository.registerUser(user).then();
                });


    }

    public String test(){
        return "Hello!!! GONZA!!!";
    }
}
