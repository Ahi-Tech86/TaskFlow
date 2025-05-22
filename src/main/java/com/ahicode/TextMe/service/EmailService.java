package com.ahicode.TextMe.service;

public interface EmailService {
    void sendActivationCode(String to, String activationCode);
    void sendEmailWithAttachment(String to, String subject, String text, byte[] attachment, String attachmentFilename);
}
