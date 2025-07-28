package io.mhetko.lor.mapper;

import io.mhetko.lor.dto.AppUserDTO;
import io.mhetko.lor.entity.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppUserMapper {

    @Mapping(target = "username", source = "username")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "password", source = "password")
    AppUser mapToAppUserEntity(AppUserDTO appUserDTO);

    @Mapping(target = "username", source = "username")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "password", source = "password")
    AppUserDTO mapToAppUserDTO(AppUser appUser);

}
