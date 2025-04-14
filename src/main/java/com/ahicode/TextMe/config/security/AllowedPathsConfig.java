package com.ahicode.TextMe.config.security;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AllowedPathsConfig {

    private final List<String> allowedPaths;

    public AllowedPathsConfig(List<String> allowedPaths) {
        this.allowedPaths = List.of(
                "/api/auth/register",
                "/api/auth/activate",
                "/api/auth/login"
        );
    }

    public List<String> getAllowedPaths() {
        return allowedPaths;
    }
}
