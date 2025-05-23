package com.ahicode.TextMe.service.impl;

import com.ahicode.TextMe.exception.AppException;
import com.ahicode.TextMe.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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

    @Override
    public void sendReportForAllTime(String to, String projectName, byte[] attachment) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(username);
            helper.setTo(to);
            helper.setSubject(String.format("The %s project report for all time", projectName));
            helper.setText("Please find the attached project report for all time");

            helper.addAttachment("all_time_report.pdf", new ByteArrayResource(attachment));

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new AppException("", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
