package com.ahicode.TextMe.factories;

import com.ahicode.TextMe.dtos.TemporaryUserDto;
import com.ahicode.TextMe.storage.entities.UserEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;

import static com.ahicode.TextMe.enums.AppRole.ADMIN;
import static com.ahicode.TextMe.enums.AppRole.USER;

@Component
public class UserEntityFactory {
    public UserEntity makeUserEntity(TemporaryUserDto userDto) {
        return UserEntity.builder()
                .email(userDto.getEmail())
                .nickname(userDto.getNickname())
                .firstname(userDto.getFirstname())
                .lastname(userDto.getLastname())
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
                .role(ADMIN)
                .createAt(Instant.now())
                .build();
    }
}
