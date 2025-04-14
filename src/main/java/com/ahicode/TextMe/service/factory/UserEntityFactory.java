package com.ahicode.TextMe.service.factory;

import com.ahicode.TextMe.model.dto.TemporaryUserDto;
import com.ahicode.TextMe.model.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;

import static com.ahicode.TextMe.model.enums.AppRole.ADMIN;
import static com.ahicode.TextMe.model.enums.AppRole.USER;

@Component
public class UserEntityFactory {
    public UserEntity makeUserEntity(TemporaryUserDto userDto) {
        return UserEntity.builder()
                .email(userDto.getEmail())
                .nickname(userDto.getNickname())
                .firstname(userDto.getFirstname())
                .lastname(userDto.getLastname())
                .password(userDto.getPassword())
                .role(USER)
                .createAt(Instant.now())
                .build();
    }

    public UserEntity makeAdminUserEntity(TemporaryUserDto userDto) {
        return UserEntity.builder()
                .email(userDto.getEmail())
                .nickname(userDto.getNickname())
                .firstname(userDto.getFirstname())
                .lastname(userDto.getLastname())
                .password(userDto.getPassword())
                .role(ADMIN)
                .createAt(Instant.now())
                .build();
    }
}
