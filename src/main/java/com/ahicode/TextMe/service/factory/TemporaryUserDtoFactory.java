package com.ahicode.TextMe.service.factory;

import com.ahicode.TextMe.model.dto.SignUpRequest;
import com.ahicode.TextMe.model.dto.TemporaryUserDto;
import org.springframework.stereotype.Component;

@Component
public class TemporaryUserDtoFactory {
    public TemporaryUserDto makeTemporaryUserDto(SignUpRequest signUpRequest, String confirmationCode) {
        return TemporaryUserDto.builder()
                .email(signUpRequest.getEmail())
                .nickname(signUpRequest.getNickname())
                .firstname(signUpRequest.getFirstname())
                .lastname(signUpRequest.getLastname())
                .password(signUpRequest.getPassword())
                .confirmationCode(confirmationCode)
                .build();
    }
}
