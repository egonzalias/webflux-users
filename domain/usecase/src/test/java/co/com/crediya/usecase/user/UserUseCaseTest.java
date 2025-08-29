package co.com.crediya.usecase.user;

import co.com.crediya.model.exception.ValidationException;
import co.com.crediya.model.user.Role;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.LoggerService;
import co.com.crediya.model.user.gateways.PasswordService;
import co.com.crediya.model.user.gateways.RoleRepository;
import co.com.crediya.model.user.gateways.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserUseCaseTest {

    @Mock
    private UserRepository repository;
    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordService passwordService;

    @Mock
    LoggerService loggerService;

    private User user;
    private Role role;
    private UserUseCase userUseCase;


    @BeforeEach
    void setup(){
        user = User.builder()
                .id(null)
                .document_number("123456789")
                .first_name("John")
                .last_name("Doe")
                .birth_date(LocalDate.of(1995, 5, 20))
                .address("123 Main St")
                .phone("+1234567890")
                .email("john.doe@example.com")
                .base_salary(BigDecimal.valueOf(1400000))
                .password("123456")
                .role("ADMIN")
                .build();
        role = Role.builder()
                .id(1L)
                .name("ADMIN")
                .description("")
                .build();

        userUseCase = new UserUseCase(repository, roleRepository, passwordService, loggerService);
    }

    @Test
    void shouldThrowValidationException_whenRoleDoesNotExist() {
        when(roleRepository.findRoleByName(anyString())).thenReturn(Mono.empty());
        when(repository.findByEmail(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(userUseCase.registerUser(user)).expectErrorSatisfies(error ->{
            ValidationException ve = (ValidationException) error;
            Assertions.assertInstanceOf(ValidationException.class, error);
            Assertions.assertTrue(ve.getErrors().stream().anyMatch( msg -> msg.contains("El rol")));
            Assertions.assertTrue(ve.getErrors().stream().anyMatch( msg -> msg.contains("es incorrecto o no existe en la base de datos.")));
        }).verify();
    }

    @Test
    void shouldThrowValidationException_whenEmailAlreadyExists() {
        when(roleRepository.findRoleByName(anyString())).thenReturn(Mono.just(role));
        when(repository.findByEmail(anyString())).thenReturn(Mono.just(user));

        StepVerifier.create(userUseCase.registerUser(user)).expectErrorSatisfies(error ->{
            ValidationException ve = (ValidationException) error;
            Assertions.assertInstanceOf(ValidationException.class, error);
            Assertions.assertTrue(ve.getErrors().stream().anyMatch( msg -> msg.contains("El correo electrónico ya está registrado")));
        }).verify();
    }

    @Test
    void shouldRegisterUserSuccessfully_whenRoleAndEmailAreValid() {
        when(roleRepository.findRoleByName(anyString())).thenReturn(Mono.just(role));
        when(repository.findByEmail(anyString())).thenReturn(Mono.empty());
        when(passwordService.encode(anyString())).thenReturn("hashedPassword");
        when(repository.registerUser(any(User.class))).thenReturn(Mono.empty());

        StepVerifier.create(userUseCase.registerUser(user))
                .verifyComplete();
    }

    @Test
    void shouldReturnUser_whenEmailExists() {
        when(repository.findByEmail(anyString())).thenReturn(Mono.just(user));

        StepVerifier.create(userUseCase.loginUser("test@test.com"))
                .expectNextMatches(u ->
                                u.getEmail().equals(user.getEmail()) &&
                                        u.getRole().equals(user.getRole()) &&
                                        u.getPassword().equals(user.getPassword())
                        )
                .verifyComplete();
    }

    @Test
    void shouldReturnUser_whenEmailDoesNotExists() {
        when(repository.findByEmail(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(userUseCase.loginUser("testnotexists@test.com"))
                .expectNextMatches(u ->
                        u.getEmail().equals(user.getEmail()) &&
                                u.getRole().equals(user.getRole()) &&
                                u.getPassword().equals(user.getPassword())
                )
                .verifyComplete();
    }
}
