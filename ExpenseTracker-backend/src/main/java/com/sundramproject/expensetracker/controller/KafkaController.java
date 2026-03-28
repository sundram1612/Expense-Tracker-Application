package com.sundramproject.expensetracker.controller;

import com.sundramproject.expensetracker.integration.kafka.ReportKafkaProducer;
import com.sundramproject.expensetracker.model.dto.ReportRequestDTO;
import com.sundramproject.expensetracker.model.entity.User;
import com.sundramproject.expensetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/kafka")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class KafkaController {

    private final ReportKafkaProducer kafkaProducer;
    private final UserRepository userRepository;

    @PostMapping("/report/async")
    public ResponseEntity<String> generateAsyncReport(@RequestBody ReportRequestDTO request, Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        request.setUserId(user.getId());
        kafkaProducer.sendReportRequest(request);

        return ResponseEntity.ok("Report generation for " + request.getReportType() + " started. You will receive an email once it is ready.");
    }
}
