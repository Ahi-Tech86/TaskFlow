package com.ahicode.TextMe.service;

public interface EmailService {
    void sendActivationCode(String to, String activationCode);
    void sendReportForAllTime(String to, String projectName, byte[] attachment);
}
