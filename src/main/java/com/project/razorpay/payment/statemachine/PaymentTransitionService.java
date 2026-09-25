package com.project.razorpay.payment.statemachine;

import com.project.razorpay.common.enums.PaymentActor;
import com.project.razorpay.common.enums.PaymentEvent;
import com.project.razorpay.common.enums.PaymentStatus;
import com.project.razorpay.payment.entity.Payment;
import com.project.razorpay.payment.entity.PaymentTransitionLog;
import com.project.razorpay.payment.repository.PaymentTransitionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentTransitionService {

    private final PaymentTransitionLogRepository paymentTransitionLogRepository;
    private final PaymentStateMachine paymentStateMachine;

    public PaymentStatus apply(Payment payment, PaymentEvent event) {

        PaymentStatus current = payment.getStatus();
        PaymentStatus next = paymentStateMachine.transition(current, event);
        payment.setStatus(next);

        PaymentTransitionLog log = PaymentTransitionLog.builder()
                .payment(payment)
                .fromStatus(current)
                .event(event)
                .toStatus(next)
                .actor(PaymentActor.SYSTEM) //TODO: fetch merchant context to identify actor
                .occurredAt(LocalDateTime.now())
                .build();

        paymentTransitionLogRepository.save(log);

        return next;
    }
}
