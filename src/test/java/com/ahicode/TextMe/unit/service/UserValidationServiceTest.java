package com.ahicode.TextMe.unit.service;

import com.ahicode.TextMe.exception.AppException;
import com.ahicode.TextMe.model.entity.UserEntity;
import com.ahicode.TextMe.repository.UserRepository;
import com.ahicode.TextMe.service.impl.UserValidationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserValidationServiceTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserValidationServiceImpl service;

    @Test
    void testIsEmailUnique_Success() {
        String email = "unique-email";
        when(repository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatCode(() -> service.isEmailUnique(email)).doesNotThrowAnyException();
    }

    @Test
    void testIsEmailUnique_Failure() {
        String email = "user@mail.com";
        UserEntity existingUser = new UserEntity();
        existingUser.setEmail(email);
        when(repository.findByEmail(email)).thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> service.isEmailUnique(email))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(String.format("User with email %s is already exists", email))
                .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST);
    }

    @Test
    void testIsNicknameUnique_Success() {
        String nickname = "unique-nickname";
        when(repository.findByNickname(nickname)).thenReturn(Optional.empty());

        assertThatCode(() -> service.isNicknameUnique(nickname)).doesNotThrowAnyException();
    }

    @Test
    void testIsNicknameUnique_Failure() {
        String nickname = "billy";
        UserEntity existingUser = new UserEntity();
        existingUser.setNickname(nickname);
        when(repository.findByNickname(nickname)).thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> service.isNicknameUnique(nickname))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(String.format("User with nickname %s is already exists", nickname))
                .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST);
    }

    @Test
    void testIsUserExistsByEmail_Success() {
        String email = "user@mail.com";
        UserEntity expectedUser = new UserEntity();
        expectedUser.setEmail(email);

        when(repository.findByEmail(email)).thenReturn(Optional.of(expectedUser));

        UserEntity actualUser = service.isUserExistsByEmail(email);

        assertThat(actualUser).isEqualTo(actualUser);
    }

    @Test
    void testIsUserExistsByEmail_Failure() {
        String email = "user@mail.com";

        when(repository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.isUserExistsByEmail(email))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(String.format("User with email %s doesn't exists", email))
                .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND);
    }
}
