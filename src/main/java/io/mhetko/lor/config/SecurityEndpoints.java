package io.mhetko.lor.config;

public class SecurityEndpoints {
    public static final String[] PUBLIC_ENDPOINTS = {
            "/api/users/register",
            "/api/users/confirm",
            "/api/users/login",
            "/api/categories/**",
            "/api/tags/**"
    };
}
