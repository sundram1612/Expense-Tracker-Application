package com.sundramproject.ExpenseTracker_backend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Service
@RequiredArgsConstructor
@AllArgsConstructor
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendMail(String to, String name, String resetLink){
        try{
            String template = Files.readString(
                    new ClassPathResource("templates/reset-password-email.html").getFile().toPath(), StandardCharsets.UTF_8
            );

            String htmlContent = String.format(template, name, resetLink, resetLink);

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject("Reset Your Password | Expense Tracker");
            mimeMessageHelper.setText(htmlContent, true);
            mimeMessageHelper.setFrom("thingparkers69@gmail.com");

            javaMailSender.send(mimeMessage);
        }
        catch (IOException | MessagingException e){
            throw new RuntimeException("Failed to send reset email ",e);
        }
    }
}
