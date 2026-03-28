package com.sundramproject.expensetracker.integration.kafka;

import com.sundramproject.expensetracker.model.dto.ReportRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportKafkaProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendReportRequest(ReportRequestDTO request) {
        kafkaTemplate.send("report-requests", request);
    }
}
