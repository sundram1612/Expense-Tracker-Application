package com.sundramproject.expensetracker.integration.kafka;

import com.sundramproject.expensetracker.model.dto.NotificationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationKafkaProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendNotification(NotificationDTO notification) {
        kafkaTemplate.send("notifications", notification);
    }
}
