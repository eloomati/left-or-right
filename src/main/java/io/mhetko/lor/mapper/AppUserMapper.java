package io.mhetko.lor.mapper;

import io.mhetko.lor.dto.AppUserDTO;
import io.mhetko.lor.dto.RegisterUserDTO;
import io.mhetko.lor.entity.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppUserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "avatarUrl", ignore = true)
    @Mapping(target = "country", ignore = true)
    @Mapping(target = "continent", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "activationToken", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "followedTopics", ignore = true)
    AppUser mapToAppUserEntity(RegisterUserDTO registerUserDTO);

    @Mapping(target = "confirmEmail", ignore = true)
    @Mapping(target = "confirmPassword", ignore = true)
    @Mapping(target = "termsAccepted", ignore = true)
    RegisterUserDTO mapToRegisterUserDTO(AppUser appUser);

    AppUserDTO mapToAppUserDTO(AppUser appUser);
}