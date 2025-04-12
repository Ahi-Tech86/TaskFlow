package com.ahicode.TextMe.services.impl;

import com.ahicode.TextMe.enums.JwtTokenType;
import com.ahicode.TextMe.services.JwtService;
import com.ahicode.TextMe.utils.JwtKeyProvider;
import com.ahicode.TextMe.utils.JwtProperties;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.SignatureAlgorithm;
import com.ahicode.TextMe.enums.AppRole;
import lombok.extern.slf4j.Slf4j;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.security.Key;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class JwtServiceImpl implements JwtService {

    private final Long refreshTokenExpirationTime;
    private final Long accessTokenExpirationTime;
    private final Key refreshTokenSignKey;
    private final Key accessTokenSignKey;

    public JwtServiceImpl(JwtKeyProvider jwtKeyProvider, JwtProperties jwtProperties) {
        this.refreshTokenSignKey = jwtKeyProvider.getSignKey(jwtProperties.getRefreshToken().getSecretKey());
        this.accessTokenSignKey = jwtKeyProvider.getSignKey(jwtProperties.getAccessToken().getSecretKey());
        this.refreshTokenExpirationTime = jwtProperties.getRefreshToken().getExpiration();
        this.accessTokenExpirationTime = jwtProperties.getAccessToken().getExpiration();
    }

    @Override
    public AppRole extractRoleFromAccessToken(String token) {
        Claims claims = extractAllClaims(token, JwtTokenType.ACCESS);
        String role = claims.get("role", String.class);
        return AppRole.valueOf(role);
    }

    @Override
    public AppRole extractRoleFromRefreshToken(String token) {
        Claims claims = extractAllClaims(token, JwtTokenType.REFRESH);
        String role = claims.get("role", String.class);
        return AppRole.valueOf(role);
    }

    @Override
    public String extractEmailFromAccessToken(String token) {
        return extractAllClaims(token, JwtTokenType.ACCESS).getSubject();
    }

    @Override
    public String extractEmailFromRefreshToken(String token) {
        return extractAllClaims(token, JwtTokenType.REFRESH).getSubject();
    }

    @Override
    public boolean isAccessTokenValid(String token) {
        return validateToken(token, JwtTokenType.ACCESS);
    }

    @Override
    public boolean isRefreshTokenValid(String token) {
        return validateToken(token, JwtTokenType.REFRESH);
    }

    @Override
    public boolean isAccessTokenExpired(String token) {
        return expirationValidate(token, JwtTokenType.ACCESS);
    }

    @Override
    public boolean isRefreshTokenExpired(String token) {
        return expirationValidate(token, JwtTokenType.REFRESH);
    }

    @Override
    public Authentication authenticateAccessToken(String token) {
        return authenticatedValidation(token);
    }

    @Override
    public String generateAccessToken(String email, AppRole role) {
        return generateToken(email, role, JwtTokenType.ACCESS);
    }

    @Override
    public String generateRefreshToken(String email, AppRole role) {
        return generateToken(email, role, JwtTokenType.REFRESH);
    }


    private Key resolveSignKey(JwtTokenType tokenType) {
        return tokenType == JwtTokenType.ACCESS ? accessTokenSignKey : refreshTokenSignKey;
    }

    private Long resolveExpirationTime(JwtTokenType tokenType) {
        return tokenType == JwtTokenType.ACCESS ? accessTokenExpirationTime : refreshTokenExpirationTime;
    }

    private boolean validateToken(String token, JwtTokenType tokenType) {
        Key signKey = resolveSignKey(tokenType);
        try {
            Jwts.parserBuilder()
                    .setSigningKey(signKey)
                    .build()
                    .parseClaimsJws(token);

            return true;

        } catch (RuntimeException e) {
            log.error("Attempt to validate token with wrong signed token {} with exception: {}", token, e.getMessage());
            return false;
        }
    }

    private Claims extractAllClaims(String token, JwtTokenType tokenType) {
        Key signKey = resolveSignKey(tokenType);
        return Jwts.parserBuilder()
                .setSigningKey(signKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean expirationValidate(String token, JwtTokenType tokenType) {
        Date expirationDate = extractAllClaims(token, tokenType).getExpiration();

        return expirationDate.before(new Date());
    }

    private Authentication authenticatedValidation(String token) {
        Claims claims = extractAllClaims(token, JwtTokenType.ACCESS);

        String email = claims.getSubject();

        return new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());
    }

    private String generateToken(String email, AppRole role, JwtTokenType tokenType) {
        Key signKey = resolveSignKey(tokenType);
        Long expirationTime = resolveExpirationTime(tokenType);

        Instant currentTime = Instant.now();
        long currentTimeMillis = currentTime.toEpochMilli();

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(Date.from(currentTime))
                .setExpiration(new Date(currentTimeMillis + expirationTime))
                .signWith(signKey, SignatureAlgorithm.HS256)
                .compact();
    }
}
