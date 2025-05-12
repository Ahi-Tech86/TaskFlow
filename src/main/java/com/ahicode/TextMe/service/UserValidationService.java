package com.ahicode.TextMe.service;

import com.ahicode.TextMe.model.entity.UserEntity;

public interface UserValidationService {
    void isEmailUnique(String email);
    void isNicknameUnique(String nickname);
    UserEntity isUserExistsByEmail(String email);
    UserEntity isUserExistsByNickname(String nickname);
}
