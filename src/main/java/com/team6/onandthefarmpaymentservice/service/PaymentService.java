package com.team6.onandthefarmpaymentservice.service;


import com.team6.onandthefarmpaymentservice.dto.PaymentDto;
import com.team6.onandthefarmpaymentservice.entity.Payment;

public interface PaymentService {
    Payment createPayment(PaymentDto paymentDto);

    Boolean isAlreadyProcessedOrderId(String orderSerial);

    Boolean createDlqPayment(PaymentDto paymentDto);
}
