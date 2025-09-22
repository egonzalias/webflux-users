package co.com.crediya.api.config;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import co.com.crediya.model.user.JwtUserInfo;
import co.com.crediya.model.user.gateways.TokenService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter implements WebFilter {

    private final TokenService tokenService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String path = exchange.getRequest().getURI().getPath();
        System.out.println("Authorization header path : " + path);
        List<String> publicPaths = List.of(
                "/api/v1/login",
                "/swagger-ui", "/swagger-ui.html", "/swagger-ui/",
                "/v3/api-docs",
                "/actuator/health",
                "/actuator/info"
        );

        //boolean isPublic = publicPaths.stream().anyMatch(path::startsWith);
        boolean isPublic = publicPaths.stream()
                .anyMatch(publicPath -> path.equals(publicPath) || path.startsWith(publicPath + "/"));
        System.out.println("Authorization header isPublic : " + isPublic);
        if (isPublic) {
            return chain.filter(exchange); // no validate token
        }

        System.out.println("Authorization header EGR: " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ") || authHeader.length() <= 7) {
            // No token provided - reject
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        String token = authHeader.substring(7);
        try {
            if (!tokenService.validateToken(token)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            JwtUserInfo userInfo = tokenService.getUserInfoFromToken(token);

            // Spring Security requires roles to be prefixed with "ROLE_"
            String role = "ROLE_" + userInfo.getRole();

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userInfo.getEmail(),
                    null,
                    List.of(new SimpleGrantedAuthority(role))
            );

            //Wrap the authentication in a SecurityContext
            SecurityContext securityContext = new SecurityContextImpl(authentication);
            //Store in reactive context
            return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));
        } catch (JwtException e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
}
