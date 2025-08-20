package io.mhetko.lor.config;

public class SecurityEndpoints {
    public static final String[] PUBLIC_ENDPOINTS = {
            "/",
            "/index",
            "/favicon.ico",
            "/css/**",
            "/js/**",
            "/images/**",
            "/api/users/register",
            "/api/users/confirm",
            "/api/users/login",
            "/api/categories/**",
            "/api/tags/**",
            "/api/comments/**",
            "/api/v1/topic-requests",
            "/api/votes/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/api/proposed-topics/**",
            "/register",
            "/register-success",
            "/api/topics/popular",
            "/proposed",
            "/profile"
    };
}