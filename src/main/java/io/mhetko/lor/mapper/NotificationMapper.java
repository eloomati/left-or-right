package io.mhetko.lor.mapper;

import io.mhetko.lor.dto.NotificationDTO;
import io.mhetko.lor.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    Notification toEntity(NotificationDTO dto);

    NotificationDTO toDto(Notification notification);
}