package com.ahicode.TextMe.service.impl;

import com.ahicode.TextMe.exception.AppException;
import com.ahicode.TextMe.model.entity.UserEntity;
import com.ahicode.TextMe.repository.UserRepository;
import com.ahicode.TextMe.service.UserValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserValidationServiceImpl implements UserValidationService {

    private final UserRepository repository;

    @Override
    public void isEmailUnique(String email) {
        checkUniqueness("email", email, repository::findByEmail, "User with email %s is already exists");
    }

    @Override
    public void isNicknameUnique(String nickname) {
        checkUniqueness("nickname", nickname, repository::findByNickname, "User with nickname %s is already exists");
    }

    @Override
    public UserEntity isUserExistsByEmail(String email) {
        return repository.findByEmail(email).orElseThrow(
                () -> {
                    log.error("Attempt to log into an account with non-existent email: {}", email);
                    throw new AppException(String.format("User with email %s doesn't exists", email), HttpStatus.NOT_FOUND);
                }
        );
    }

    private void checkUniqueness(
            String varName, String value, Function<String, Optional<UserEntity>> findFunction, String errorMessage
    ) {
        findFunction.apply(value)
                .ifPresent(
                        user -> {
                            log.error("Attempt to register with an existing {}: {}", varName, value);
                            throw new AppException(String.format(errorMessage, value), HttpStatus.BAD_REQUEST);
                        }
                );
    }
}
