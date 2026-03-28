package com.sundramproject.expensetracker.integration.kafka;

import com.sundramproject.expensetracker.model.dto.NotificationDTO;
import com.sundramproject.expensetracker.repository.UserRepository;
import com.sundramproject.expensetracker.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationKafkaConsumer {

    private final EmailService emailService;
    private final UserRepository userRepository;

    @KafkaListener(topics = "notifications", groupId = "report-group")
    public void consume(NotificationDTO notification) {
        System.out.println("Notification received for user " + notification.getUserId() + ": " + notification.getMessage());

        userRepository.findById(notification.getUserId()).ifPresent(user -> {
            // In a real app, we might push this to a WebSocket or Mobile App.
            // For now, we'll send a simple email alert if it's high priority, or just log it.
            if ("WARNING".equalsIgnoreCase(notification.getType()) || "SUCCESS".equalsIgnoreCase(notification.getType())) {
                emailService.sendSimpleNotificationEmail(user.getEmail(), notification.getTitle(), notification.getMessage());
            }
        });
    }
}
