package co.com.crediya.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, JwtAuthorizationFilter jwtAuthorizationFilter) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                                //.anyExchange().permitAll()
                        .pathMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        .pathMatchers("/public/**").permitAll()
                        .pathMatchers("/api/v1/login").permitAll()
                        .pathMatchers("/api/v1/usuarios").hasRole("ADMIN")
                        .anyExchange().authenticated()
                )
                //Here is inserted the custom filter before the standard authentication filter
                .addFilterAt(jwtAuthorizationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}
