package com.ahicode.TextMe.model.dto.auth;

import com.ahicode.TextMe.model.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private UserDto userDto;
    private String accessToken;
    private String refreshToken;
}
