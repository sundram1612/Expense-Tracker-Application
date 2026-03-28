package com.sundramproject.expensetracker.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    public void sendMail(String to, String name, String resetLink) {
        try {
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
        } catch (IOException | MessagingException e) {
            throw new RuntimeException("Failed to send reset email ", e);
        }
    }

    public void sendReportMail(String to, String reportType, byte[] reportData, String format) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(to);
            helper.setSubject(reportType + " - Expense Tracker");
            helper.setText("Hello,\n\nPlease find your requested " + reportType + " attached as a " + format + " file.");

            String filename = reportType.replace(" ", "_") + "." + (format.equalsIgnoreCase("excel") ? "xlsx" : "pdf");
            helper.addAttachment(filename, new ByteArrayResource(reportData));

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send report email", e);
        }
    }

    public void sendSimpleNotificationEmail(String to, String title, String message) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false);

            helper.setTo(to);
            helper.setSubject(title + " | Expense Tracker");
            helper.setText(message);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send notification email", e);
        }
    }
}
