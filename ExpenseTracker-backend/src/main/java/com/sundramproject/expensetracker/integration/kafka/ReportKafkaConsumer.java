package com.sundramproject.expensetracker.integration.kafka;

import com.sundramproject.expensetracker.model.dto.NotificationDTO;
import com.sundramproject.expensetracker.model.dto.ReportRequestDTO;
import com.sundramproject.expensetracker.model.entity.User;
import com.sundramproject.expensetracker.repository.UserRepository;
import com.sundramproject.expensetracker.service.EmailService;
import com.sundramproject.expensetracker.service.ReportGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportKafkaConsumer {

    private final ReportGenerationService reportGenerationService;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final NotificationKafkaProducer notificationProducer;

    @KafkaListener(topics = "report-requests", groupId = "report-group")
    public void consume(ReportRequestDTO request) throws Exception {
        System.out.println("Kafka received report request for user: " + request.getUserId());

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        byte[] reportData;
        if ("excel".equalsIgnoreCase(request.getFormat())) {
            reportData = reportGenerationService.generateExcelReport(user, request.getReportType(), request.getFrom(), request.getTo());
        } else {
            reportData = reportGenerationService.generatePdfReport(user, request.getReportType(), request.getFrom(), request.getTo());
        }

        // Send Email
        emailService.sendReportMail(user.getEmail(), request.getReportType(), reportData, request.getFormat());

        // Send Notification Event
        NotificationDTO notification = new NotificationDTO(
                user.getId(),
                "Report Ready",
                "Your " + request.getReportType() + " has been generated and sent to your email.",
                "SUCCESS"
        );
        notificationProducer.sendNotification(notification);

        System.out.println("Report sent to " + user.getEmail());
    }
}
