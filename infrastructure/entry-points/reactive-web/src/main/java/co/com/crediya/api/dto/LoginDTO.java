package co.com.crediya.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Schema(description = "Credenciales para iniciar sesión")
public class LoginDTO {

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El correo electrónico no es válido", regexp = ".+@.+\\..+")
    @Schema(description = "Correo electrónico del usuario", example = "usuario@example.com")
    private String email;
    @NotBlank(message = "La clave del usuario es obligatoria")
    @Schema(description = "Contraseña del usuario", example = "password123")
    private String password;
}
