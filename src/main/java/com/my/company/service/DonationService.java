package com.my.company.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.company.school.tsinjo_std21001.domain.*;
import com.company.school.tsinjo_std21001.repository.*;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DonationService {
    
    private final DonationRepository donationRepository;
    private final DonorRepository donorRepository;
    
    public List<Donation> getAllDonationsOrderByDateDesc() {
        return donationRepository.findAllByOrderByCreatedAtDesc();
    }
    
    public Donor getOrCreateDonor(String email, String fullName) {
        return donorRepository.findByEmail(email)
            .orElseGet(() -> {
                Donor newDonor = Donor.builder()
                    .email(email)
                    .fullName(fullName)
                    .build();
                log.info("Creating new donor: {}", email);
                return donorRepository.save(newDonor);
            });
    }
    
    public Donation createDonation(Donor donor, Payment payment) {
        Donation donation = Donation.builder()
            .donor(donor)
            .payment(payment)
            .build();
        
        Donation saved = donationRepository.save(donation);
        log.info("Created donation with ID: {} for donor: {}", saved.getId(), donor.getEmail());
        return saved;
    }
}
