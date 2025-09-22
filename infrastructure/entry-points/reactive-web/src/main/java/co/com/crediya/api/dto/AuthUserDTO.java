package co.com.crediya.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Schema(description = "Respuesta al autenticar al usuario")
public class AuthUserDTO {
    //private String email;
    //private String password;
    //private String role;
    @Schema(description = "Token JWT generado para el usuario", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6...")
    private String jwtToken;
    //private Instant expiresAt;
}
