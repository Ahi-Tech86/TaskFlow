package com.ahicode.TextMe.service.impl;

import com.ahicode.TextMe.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    @Value("${spring.mail.username}")
    private String username;
    private final JavaMailSender mailSender;

    @Override
    public void sendActivationCode(String to, String activationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(username);
        message.setTo(to);
        message.setSubject("Account activation code");
        message.setText(
                String.format(
                        "Here is your activation code for your registration on our website. Confirmation code: %s",
                        activationCode
                )
        );
        mailSender.send(message);
    }
}
