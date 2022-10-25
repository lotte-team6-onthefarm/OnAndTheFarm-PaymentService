package com.team6.onandthefarmpaymentservice.service;

import com.example.tccpayment.dto.PaymentDto;
import com.example.tccpayment.entity.Payment;

public interface PaymentService {
    Payment createPayment(PaymentDto paymentDto);

    Boolean isAlreadyProcessedOrderId(String orderSerial);
}
