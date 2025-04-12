package com.ahicode.TextMe.services;

import com.ahicode.TextMe.dtos.*;

public interface AuthService {
    String register(SignUpRequest singUpRequest);
    AuthResponse login(SignInRequest signInRequest);
    UserDto activateRegistration(ActivateRegistrationRequest activateRegistrationRequest);
}
