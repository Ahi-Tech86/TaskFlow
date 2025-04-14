package com.ahicode.TextMe.service.factory.user;

import com.ahicode.TextMe.model.dto.user.UserDto;
import com.ahicode.TextMe.model.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserDtoFactory {
    public UserDto makeUserDto(UserEntity entity) {
        return UserDto.builder()
                .email(entity.getEmail())
                .nickname(entity.getNickname())
                .firstname(entity.getFirstname())
                .lastname(entity.getLastname())
                .build();
    }
}
