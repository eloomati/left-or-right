package io.mhetko.lor.dto;

import lombok.Data;

@Data
public class AppUserDTO {
    private Long id;
    private String username;
    private String email;
    private String avatarUrl;
    private String country;
    private String continent;
}
