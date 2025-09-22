package co.com.crediya.api.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Builder
@Schema(description = "Respuesta de error estandarizada")
public class ErrorResponse {
    @Schema(description = "Fecha y hora del error")
    private ZonedDateTime timestamp;
    @Schema(description = "Ruta del endpoint que falló")
    private String path;
    @Schema(description = "Codigo HTTP")
    private int status;
    @Schema(description = "Descripción del error HTTP")
    private String error;
    @Schema(description = "Lista de errores específicos")
    private List<String> errors;
    @Schema(description = "ID único de la solicitud")
    private String requestId;
}
