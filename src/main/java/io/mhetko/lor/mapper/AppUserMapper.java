package io.mhetko.lor.mapper;

import io.mhetko.lor.dto.RegisterUserDTO;
import io.mhetko.lor.entity.AppUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AppUserMapper {
    AppUser mapToAppUserEntity(RegisterUserDTO registerUserDTO);
    RegisterUserDTO mapToAppUserDTO(AppUser appUser);
}