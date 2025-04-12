package com.ahicode.TextMe.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "application.security.jwt")
public class JwtProperties {

    private AccessTokenProperties accessToken = new AccessTokenProperties();
    private RefreshTokenProperties refreshToken = new RefreshTokenProperties();

    public static class AccessTokenProperties {
        private String secretKey;
        private Long expiration;

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public Long getExpiration() {
            return expiration;
        }

        public void setExpiration(Long expiration) {
            this.expiration = expiration;
        }
    }

    public static class RefreshTokenProperties {
        private String secretKey;
        private Long expiration;

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public Long getExpiration() {
            return expiration;
        }

        public void setExpiration(Long expiration) {
            this.expiration = expiration;
        }
    }

    public AccessTokenProperties getAccessToken() {
        return accessToken;
    }

    public RefreshTokenProperties getRefreshToken() {
        return refreshToken;
    }
}
