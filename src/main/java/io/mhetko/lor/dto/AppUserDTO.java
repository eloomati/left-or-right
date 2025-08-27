package io.mhetko.lor.dto;

import io.mhetko.lor.entity.enums.Role;
import lombok.Data;

@Data
public class AppUserDTO {
    private Long id;
    private String username;
    private String email;
    private String avatarUrl;
    private String country;
    private String continent;
    private Role role;
}
