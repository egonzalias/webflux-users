package co.com.crediya.api.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoginDTO {

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El correo electrónico no es válido", regexp = ".+@.+\\..+")
    private String email;
    @NotBlank(message = "La clave del usuario es obligatoria")
    private String password;
}
