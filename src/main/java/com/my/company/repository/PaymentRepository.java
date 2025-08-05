package com.my.company.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.company.school.tsinjo_std21001.domain.Payment;
import com.company.school.tsinjo_std21001.domain.PaymentStatus;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    List<Payment> findByStatus(PaymentStatus status);
}
