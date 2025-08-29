package co.com.crediya.api;


import co.com.crediya.api.dto.UserDTO;
import co.com.crediya.model.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/usuarios", method = RequestMethod.POST,
                    beanClass = Handler.class, beanMethod = "registerUser",
                    operation = @Operation(
                            summary = "Register a new user",
                            requestBody = @RequestBody(
                                    description = "User data to register",
                                    required = true,
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = UserDTO.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "User created successfully"),
                                    @ApiResponse(responseCode = "400", description = "Invalid input")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/login", method = RequestMethod.POST,
                    beanClass = Handler.class, beanMethod = "loginUser",
                    operation = @Operation(summary = "Login a user")
            )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST("/api/v1/usuarios"), handler::registerUser)
                .and(route(POST("/api/v1/login"), handler::loginUser));
    }
}
