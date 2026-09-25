package com.project.razorpay.payment.repository;

import com.project.razorpay.payment.entity.PaymentTransitionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentTransitionLogRepository extends JpaRepository<PaymentTransitionLog, Long> {
}
