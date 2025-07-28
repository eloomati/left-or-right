package io.mhetko.lor.mapper;

import io.mhetko.lor.dto.AppUserDTO;
import io.mhetko.lor.entity.AppUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AppUserMapper {
    AppUser mapToAppUserEntity(AppUserDTO appUserDTO);
    AppUserDTO mapToAppUserDTO(AppUser appUser);
}