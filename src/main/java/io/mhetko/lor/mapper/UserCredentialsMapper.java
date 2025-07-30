package io.mhetko.lor.mapper;

import io.mhetko.lor.dto.UserCredentialsDTO;
import io.mhetko.lor.entity.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserCredentialsMapper {
    @Mapping(source = "isActive", target = "isActive")
    UserCredentialsDTO toDto(AppUser appUser);
}