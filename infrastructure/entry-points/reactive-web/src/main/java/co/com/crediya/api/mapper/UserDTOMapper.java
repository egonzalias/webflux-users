package co.com.crediya.api.mapper;

import co.com.crediya.api.dto.AuthUserDTO;
import co.com.crediya.api.dto.LoginDTO;
import co.com.crediya.api.dto.UserDTO;
import co.com.crediya.model.user.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserDTOMapper {
    UserDTO toResponse(User user);
    AuthUserDTO toAuthResponse(User user);
    User toModel(UserDTO userDTO);
    User toModel(LoginDTO loginDTO);
}
