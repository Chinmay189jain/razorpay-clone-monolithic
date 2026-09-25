package com.project.razorpay.payment.processor.strategy;

import com.project.razorpay.common.util.RandomizerUtil;
import com.project.razorpay.payment.processor.PaymentProcessor;
import com.project.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.project.razorpay.payment.processor.dto.PaymentProcessorResponse;
import org.springframework.stereotype.Component;

@Component
public class UpiPaymentProcessor implements PaymentProcessor {
    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {
        final String VPA_CODE_FAIL = "fail@okaxis";

        String vpa = request.methodDetails() != null && request.methodDetails().get("vpa") != null
                ? request.methodDetails().get("vpa").toString()
                : null;

        // simulation
        if (VPA_CODE_FAIL.equals(vpa)) {
            return new PaymentProcessorResponse.Failure("UPI_REJECTED",
                    "Bank rejected the transaction registration"
            );
        }

        String processorRef = "UPI_PROCESSOR_"+ RandomizerUtil.randomBase64(16);

        String bankRef = "BANK_REF"+ RandomizerUtil.randomBase64(16);

        return new PaymentProcessorResponse.Success(processorRef, bankRef);
    }
}
