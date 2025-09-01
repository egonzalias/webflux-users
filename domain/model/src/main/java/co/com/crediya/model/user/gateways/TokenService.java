package co.com.crediya.model.user.gateways;

import co.com.crediya.model.user.JwtUserInfo;
import co.com.crediya.model.user.User;

public interface TokenService {
    String generateToken(User user);
    boolean validateToken(String token);
    JwtUserInfo getUserInfoFromToken(String token);
}
