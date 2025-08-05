package com.my.company.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.company.school.tsinjo_std21001.domain.*;
import com.company.school.tsinjo_std21001.dto.PaymentVerificationResponse;
import com.company.school.tsinjo_std21001.repository.PaymentRepository;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VolaPaymentVerificationService {
    
    @Value("${vola.api.key}")
    private String volaApiKey;
    
    @Value("${vola.api.url}")
    private String volaApiUrl;
    
    private final PaymentRepository paymentRepository;
    private final WebClient webClient = WebClient.builder().build();
    
    public Payment submitPaymentForVerification(String paymentId) {
        log.info("Submitting payment {} for verification", paymentId);
        
        Payment payment = Payment.builder()
            .id(paymentId)
            .status(PaymentStatus.VERIFYING)
            .date(LocalDateTime.now())
            .build();
        
        return paymentRepository.save(payment);
    }
    
    @Scheduled(fixedDelay = 30000) // Vérifie toutes les 30 secondes
    public void checkPendingPayments() {
        List<Payment> pendingPayments = paymentRepository.findByStatus(PaymentStatus.VERIFYING);
        
        log.debug("Checking {} pending payments", pendingPayments.size());
        
        pendingPayments.forEach(this::verifyPayment);
    }
    
    private void verifyPayment(Payment payment) {
        try {
            PaymentVerificationResponse response = webClient.get()
                .uri(volaApiUrl + "/payments/" + payment.getId())
                .header("Authorization", "Bearer " + volaApiKey)
                .retrieve()
                .bodyToMono(PaymentVerificationResponse.class)
                .block();
            
            if (response != null && !response.getStatus().equals("VERIFYING")) {
                updatePaymentFromResponse(payment, response);
                log.info("Payment {} status updated to {}", payment.getId(), payment.getStatus());
            }
        } catch (Exception e) {
            log.error("Error verifying payment {}: {}", payment.getId(), e.getMessage());
        }
    }
    
    private void updatePaymentFromResponse(Payment payment, PaymentVerificationResponse response) {
        payment.setStatus(PaymentStatus.valueOf(response.getStatus()));
        payment.setAmount(response.getAmount());
        payment.setPaymentMethod(response.getPaymentMethod());
        payment.setDate(response.getDate());
        paymentRepository.save(payment);
    }
}
