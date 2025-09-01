package co.com.crediya.api;


import co.com.crediya.api.dto.LoginDTO;
import co.com.crediya.api.dto.UserDTO;
import co.com.crediya.model.exception.ValidationException;
import co.com.crediya.api.mapper.UserDTOMapper;
import co.com.crediya.model.user.User;
import co.com.crediya.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class Handler {

    private final UserUseCase userUseCase;
    private final UserDTOMapper userDTOMapper;
    private final Validator validator;

    public Mono<ServerResponse> registerUser(ServerRequest serverRequest) {
        return serverRequest
                .bodyToMono(UserDTO.class)
                .doOnNext(this::validate)
                .map(userDTOMapper::toModel)
                .flatMap(userUseCase::registerUser)
                .then(ServerResponse.status(HttpStatus.CREATED).build());

        /*return ReactiveSecurityContextHolder.getContext()
                .flatMap(context -> {
                    Authentication auth = context.getAuthentication();
                    boolean isAdmin = auth.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                    if (!isAdmin) {
                        return ServerResponse.status(HttpStatus.FORBIDDEN).build();
                    }
                    return serverRequest.bodyToMono(UserDTO.class)
                            .doOnNext(this::validate)
                            .map(userDTOMapper::toModel)
                            .flatMap(userUseCase::registerUser)
                            .then(ServerResponse.status(HttpStatus.CREATED).build());
                });*/
    }

    public Mono<ServerResponse> authenticateUser(ServerRequest serverRequest) {
        return serverRequest
                .bodyToMono(LoginDTO.class)
                .doOnNext(this::validate)
                .map(userDTOMapper::toModel)
                .flatMap(userUseCase::authenticateUser)
                .map(userDTOMapper::toAuthResponse)
                .flatMap(user -> ServerResponse.ok().bodyValue(user))
                .switchIfEmpty(ServerResponse.status(HttpStatus.NOT_FOUND).build());
    }

    private void validate(Object objDTO) {
        BindingResult errors = new BeanPropertyBindingResult(objDTO, objDTO.getClass().getName());
        validator.validate(objDTO, errors);
        if (errors.hasErrors()) {
            List<String> messages = errors.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            throw new ValidationException(messages);
        }
    }
}
