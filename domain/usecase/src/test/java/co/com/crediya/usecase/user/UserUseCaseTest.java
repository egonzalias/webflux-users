package co.com.crediya.usecase.user;

import co.com.crediya.model.exception.ValidationException;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.PasswordService;
import co.com.crediya.model.user.gateways.RoleRepository;
import co.com.crediya.model.user.gateways.UserRepository;
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

    private User user;
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
                .email("john2.doe@example.com")
                .base_salary(BigDecimal.valueOf(1400000))
                .password("123456")
                .role_id("ADMIN")
                .build();
        userUseCase = new UserUseCase(repository, roleRepository, passwordService);
    }

    @Test
    void shouldRegisterUser_whenEmailNotExists() {
        when(repository.findByEmail(user.getEmail())).thenReturn(Mono.just(false));
        when(repository.registerUser(user)).thenReturn(Mono.empty());
        StepVerifier.create(userUseCase.registerUser(user)).verifyComplete();
        verify(repository).registerUser(user);
    }

    @Test
    void shouldThrowError_whenEmailAlreadyExists() {
        when(repository.findByEmail(user.getEmail())).thenReturn(Mono.just(true));
        StepVerifier.create(userUseCase.registerUser(user))
                .expectErrorMatches(throwable ->
                        throwable instanceof ValidationException &&
                                ((ValidationException) throwable).getErrors() != null &&
                                ((ValidationException) throwable).getErrors().contains("El correo electrónico ya está registrado")
                )
                .verify();

        verify(repository, never()).registerUser(any());
    }
}
