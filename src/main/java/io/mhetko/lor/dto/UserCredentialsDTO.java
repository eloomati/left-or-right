package io.mhetko.lor.dto;

public record UserCredentialsDTO(
        String username,
        String password,
        Boolean isActive
) {}