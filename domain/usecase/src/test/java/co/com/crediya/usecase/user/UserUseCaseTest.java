package co.com.crediya.usecase.user;

import co.com.crediya.model.exception.ValidationException;
import co.com.crediya.model.user.Role;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.*;
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
    private TokenService tokenService;

    @Mock
    LoggerService loggerService;

    private User user;
    private Role role;
    private UserUseCase userUseCase;


    @BeforeEach
    void setup(){
        userUseCase = new UserUseCase(repository, roleRepository, passwordService, tokenService, loggerService);
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
                .password("hashedPassword123")
                .role("ADMIN")
                .build();
        role = Role.builder()
                .id(1L)
                .name("ADMIN")
                .description("")
                .build();
    }

    @Test
    void shouldThrowValidationException_whenRoleDoesNotExist() {
        when(roleRepository.findRoleByName(anyString())).thenReturn(Mono.empty());

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
        when(repository.findByDocumentNumber(anyString())).thenReturn(Mono.empty());

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
        when(repository.findByDocumentNumber(anyString())).thenReturn(Mono.empty());
        when(passwordService.encode(anyString())).thenReturn("hashedPassword123");
        when(repository.registerUser(any(User.class))).thenReturn(Mono.empty());

        StepVerifier.create(userUseCase.registerUser(user))
                .verifyComplete();
    }

    @Test
    void shouldReturnJwtToken_whenAuthenticationIsSuccessful() {
        User loginAttempt = user.toBuilder()
                .email("john.doe@example.com")
                .password("123456")
                .build();

        when(repository.findByEmail(loginAttempt.getEmail())).thenReturn(Mono.just(user));
        when(passwordService.matches("123456", "hashedPassword123")).thenReturn(true);
        when(tokenService.generateToken(user)).thenReturn("mocked-jwt-token");

        StepVerifier.create(userUseCase.authenticateUser(loginAttempt))
                .expectNextMatches(u ->
                                "mocked-jwt-token".equals(u.getJwtToken())
                        )
                .verifyComplete();
    }

    @Test
    void shouldThrowValidationException_whenEmailNotFound() {
        User loginAttempt = user.toBuilder()
                .email("test@crediya.com")
                .password("123456")
                .build();

        when(repository.findByEmail(loginAttempt.getEmail())).thenReturn(Mono.empty());

        StepVerifier.create(userUseCase.authenticateUser(loginAttempt))
                .expectErrorSatisfies(error ->{
                    ValidationException ve = (ValidationException) error;
                    Assertions.assertTrue(ve.getErrors().stream().anyMatch( msg -> msg.contains("El correo electrónico no esta registrado")));
                })
                .verify();
    }

    @Test
    void shouldThrowValidationException_whenPasswordIsWrong() {
        User loginAttempt = user.toBuilder()
                .email("test@crediya.com")
                .password("wrongPassword")
                .build();

        when(repository.findByEmail(loginAttempt.getEmail())).thenReturn(Mono.just(user));
        when(passwordService.matches(loginAttempt.getPassword(), user.getPassword())).thenReturn(false);

        StepVerifier.create(userUseCase.authenticateUser(loginAttempt))
                .expectErrorSatisfies(error ->{
                    ValidationException ve = (ValidationException) error;
                    Assertions.assertTrue(ve.getErrors().stream().anyMatch( msg -> msg.contains("La contraseña es incorrecta")));
                })
                .verify();
    }

}
