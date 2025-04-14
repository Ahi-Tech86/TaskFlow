package com.ahicode.TextMe.service;

import com.ahicode.TextMe.model.dto.auth.ActivateRegistrationRequest;
import com.ahicode.TextMe.model.dto.auth.AuthResponse;
import com.ahicode.TextMe.model.dto.auth.SignInRequest;
import com.ahicode.TextMe.model.dto.auth.SignUpRequest;
import com.ahicode.TextMe.model.dto.user.UserDto;

public interface AuthService {
    String register(SignUpRequest singUpRequest);
    AuthResponse login(SignInRequest signInRequest);
    UserDto activateRegistration(ActivateRegistrationRequest activateRegistrationRequest);
}
