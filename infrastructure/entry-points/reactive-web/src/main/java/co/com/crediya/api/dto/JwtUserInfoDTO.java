package co.com.crediya.api.dto;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class JwtUserInfoDTO {
    private String email;
    private String role;
    private String jwtToken;
    private Instant expiresAt;
}
