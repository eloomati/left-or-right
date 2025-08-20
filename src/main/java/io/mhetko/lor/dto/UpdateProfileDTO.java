package io.mhetko.lor.dto;

import lombok.Data;

@Data
public class UpdateProfileDTO {
    private String avatarUrl;
    private String country;
    private String continent;
}