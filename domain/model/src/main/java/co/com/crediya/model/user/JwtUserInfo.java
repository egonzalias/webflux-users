package co.com.crediya.model.user;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class JwtUserInfo {
    private String email;
    private String role;
    private Date expiresAt;
}
