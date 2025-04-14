package com.ahicode.TextMe.service;

import com.ahicode.TextMe.model.dto.*;

public interface AuthService {
    String register(SignUpRequest singUpRequest);
    AuthResponse login(SignInRequest signInRequest);
    UserDto activateRegistration(ActivateRegistrationRequest activateRegistrationRequest);
}
