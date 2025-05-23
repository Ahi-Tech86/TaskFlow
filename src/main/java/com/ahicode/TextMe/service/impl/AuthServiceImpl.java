package com.ahicode.TextMe.service.impl;

import com.ahicode.TextMe.model.dto.auth.ActivateRegistrationRequest;
import com.ahicode.TextMe.model.dto.auth.AuthResponse;
import com.ahicode.TextMe.model.dto.auth.SignInRequest;
import com.ahicode.TextMe.model.dto.auth.SignUpRequest;
import com.ahicode.TextMe.model.dto.user.TemporaryUserDto;
import com.ahicode.TextMe.model.dto.user.UserDto;
import com.ahicode.TextMe.model.enums.AppRole;
import com.ahicode.TextMe.exception.AppException;
import com.ahicode.TextMe.service.*;
import com.ahicode.TextMe.service.factory.auth.AuthResponseFactory;
import com.ahicode.TextMe.service.factory.user.TemporaryUserDtoFactory;
import com.ahicode.TextMe.service.factory.user.UserDtoFactory;
import com.ahicode.TextMe.service.factory.user.UserEntityFactory;
import com.ahicode.TextMe.model.entity.UserEntity;
import com.ahicode.TextMe.repository.UserRepository;
import com.ahicode.TextMe.service.generator.ActivationCodeGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final JwtService jwtService;
    private final UserDtoFactory dtoFactory;
    private final UserRepository repository;
    private final EmailService emailService;
    private final UserEntityFactory entityFactory;
    private final PasswordEncoder passwordEncoder;
    private final UserValidationService validationService;
    private final AuthResponseFactory authResponseFactory;
    private final ActivationCodeGenerator activationCodeGenerator;
    private final TemporaryUserDtoFactory temporaryUserDtoFactory;
    private final RedisTemplate<String, Integer> integerRedisTemplate;
    private final RedisTemplate<String, TemporaryUserDto> redisTemplate;

    @Autowired
    public AuthServiceImpl(
            JwtService jwtService,
            UserDtoFactory dtoFactory,
            UserRepository repository,
            EmailService emailService,
            UserEntityFactory entityFactory,
            PasswordEncoder passwordEncoder,
            UserValidationService validationService,
            AuthResponseFactory authResponseFactory,
            ActivationCodeGenerator activationCodeGenerator,
            TemporaryUserDtoFactory temporaryUserDtoFactory,
            @Qualifier("redisTemplate") RedisTemplate<String, TemporaryUserDto> redisTemplate,
            @Qualifier("integerRedisTemplate") RedisTemplate<String, Integer> integerRedisTemplate
    ) {
        this.jwtService = jwtService;
        this.dtoFactory = dtoFactory;
        this.repository = repository;
        this.emailService = emailService;
        this.entityFactory = entityFactory;
        this.passwordEncoder = passwordEncoder;
        this.validationService = validationService;
        this.authResponseFactory = authResponseFactory;
        this.activationCodeGenerator = activationCodeGenerator;
        this.temporaryUserDtoFactory = temporaryUserDtoFactory;
        this.redisTemplate = redisTemplate;
        this.integerRedisTemplate = integerRedisTemplate;
    }

    @Override
    public String register(SignUpRequest signUpRequest) {
        String email = signUpRequest.getEmail();
        String nickname = signUpRequest.getNickname();

        validationService.isEmailUnique(email);
        validationService.isNicknameUnique(nickname);

        String confirmationCode = activationCodeGenerator.generateCode();

        TemporaryUserDto temporaryUserDto = temporaryUserDtoFactory.makeTemporaryUserDto(signUpRequest, confirmationCode);
        redisTemplate.opsForValue().set(email, temporaryUserDto, 20, TimeUnit.MINUTES);
        log.info("User information with email {} is temporarily saved", email);

        try {
            emailService.sendActivationCode(email, confirmationCode);
            log.info("Message with activation code was send to email {}", email);
        } catch (RuntimeException exception) {
            log.error("Attempt to send message was unsuccessful", exception);
            throw new AppException("There was an error sending the message", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return "An activation code has been sent to your email, please send the activation code before it expires. " +
                "The activation code expires in 20 minutes.";
    }

    @Override
    public AuthResponse login(SignInRequest signInRequest) {
        String email = signInRequest.getEmail();

        String failedLoginKey = "failedLogin:" + email;
        String lockKey = "locked:" + email;

        if (isLockedLogin(lockKey)) {
            log.info("Attempting to log into your account {} when the limit of attempts has been exceeded", email);
            throw new AppException(
                    "Due to an incorrect password entry, we have temporarily blocked you from " +
                            "logging into your account. Come back later", HttpStatus.BAD_REQUEST
            );
        }

        UserEntity user = validationService.isUserExistsByEmail(email);
        String nickname = user.getNickname();

        if (passwordEncoder.matches(signInRequest.getPassword(), user.getPassword())) {
            integerRedisTemplate.delete(failedLoginKey);

            Long userId = user.getId();
            AppRole role = user.getRole();
            UserDto userDto = dtoFactory.makeUserDto(user);

            String accessToken = jwtService.generateAccessToken(userId, nickname, role);
            String refreshToken = jwtService.generateRefreshToken(userId, nickname, role);

            AuthResponse response = authResponseFactory.makeAuthResponse(userDto, accessToken, refreshToken);

            log.info("Successful login to {} account", email);
            return response;
        } else {
            handleFailedLogin(failedLoginKey, lockKey);

            log.error("Attempt to log with incorrect password to {} account", email);
            throw new AppException(
                    "Incorrect password", HttpStatus.UNAUTHORIZED
            );
        }
    }

    @Override
    @Transactional
    public UserDto activateRegistration(ActivateRegistrationRequest activateRegistrationRequest) {
        String email = activateRegistrationRequest.getEmail();
        String confirmationCode = activateRegistrationRequest.getConfirmationCode();

        TemporaryUserDto temporaryUserDto = redisTemplate.opsForValue().get(email);
        System.out.println(temporaryUserDto);
        return confirmUser(email, confirmationCode, temporaryUserDto);
    }

    private boolean isLockedLogin(String lockKey) {
        Boolean isLocked = integerRedisTemplate.hasKey(lockKey);

        return isLocked != null && isLocked;
    }

    private void handleFailedLogin(String failedLoginKey, String lockKey) {
        Integer failedAttempts = integerRedisTemplate.opsForValue().get(failedLoginKey);

        if (failedAttempts == null) {
            integerRedisTemplate.opsForValue().set(failedLoginKey, 1);
        } else {
            integerRedisTemplate.opsForValue().set(failedLoginKey, failedAttempts + 1);

            if (failedAttempts % 3 == 2) {
                integerRedisTemplate.opsForValue().set(lockKey, 5, 5, TimeUnit.MINUTES);
            }
        }
    }

    private UserDto confirmUser(String email, String confirmationCode, TemporaryUserDto temporaryUserDto) {
        if (!confirmationCode.equals(temporaryUserDto.getConfirmationCode())) {
            log.error("Attempting to activate an account with an incorrect confirmation code to email {}", email);
            throw new AppException("The confirmation code doesn't match what the server generated", HttpStatus.UNAUTHORIZED);
        }

        UserEntity user = entityFactory.makeUserEntity(temporaryUserDto);
        user.setPassword(passwordEncoder.encode(temporaryUserDto.getPassword()));

        UserEntity savedUser = repository.saveAndFlush(user);
        log.info("User with email {} was successfully saved", email);

        return dtoFactory.makeUserDto(savedUser);
    }
}
