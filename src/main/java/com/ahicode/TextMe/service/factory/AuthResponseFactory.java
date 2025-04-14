package com.ahicode.TextMe.service.factory;

import com.ahicode.TextMe.model.dto.AuthResponse;
import com.ahicode.TextMe.model.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
public class AuthResponseFactory {
    public AuthResponse makeAuthResponse(UserDto userDto, String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .userDto(userDto)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
