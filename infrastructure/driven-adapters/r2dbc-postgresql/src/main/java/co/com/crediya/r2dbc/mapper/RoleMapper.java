package co.com.crediya.r2dbc.mapper;

import co.com.crediya.model.user.Role;
import co.com.crediya.r2dbc.entity.RoleEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    Role toModel(RoleEntity entity);
    RoleEntity toEntity(Role model);
}
