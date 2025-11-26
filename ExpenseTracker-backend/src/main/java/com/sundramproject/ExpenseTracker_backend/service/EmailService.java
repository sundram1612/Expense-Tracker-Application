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
//        try{
//            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
//            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
//
//            mimeMessageHelper.setTo(to);
//            mimeMessageHelper.setSubject("Reset Your Password | Expense Tracker");
//
//            String htmlContent = """
//                    <p>Hi %s,</p>
//                    <p>You requested to reset your password. Click the button below:</p>
//                    <p style="text-align: center;">
//                        <a href="%s" style="background: #0d6efd; color: white; padding: 10px 20px; text-decoration: none; border-radius: 6px; font-weight: bold;">
//                            Reset Password
//                        </a>
//                    </p>
//                    <p>If the button does not work, open this link: </p>
//                    <p>%s</p>
//                    <br/>
//                    <p>Regards, <br/><b>Expense Tracker Team</b></p>
//                    <hr>
//                    <p style="font-size: 12px; color: gray;">
//                        If you did not request a password reset, ignore this email.
//                    </p>
//                    """.formatted(name, resetLink, resetLink);
//
//            mimeMessageHelper.setText(htmlContent, true);
//            javaMailSender.send(mimeMessage);
//        }
        try{
            // Load HTML template
            String template = Files.readString(
                    new ClassPathResource("templates/reset-password-email.html").getFile().toPath(), StandardCharsets.UTF_8
            );

            // Fill placeholders
            String htmlContent = String.format(template, name, resetLink, resetLink);

            // Send email
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
