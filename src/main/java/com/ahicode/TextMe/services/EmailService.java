package com.ahicode.TextMe.services;

public interface EmailService {
    void sendActivationCode(String to, String activationCode);
}
