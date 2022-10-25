package com.team6.onandthefarmpaymentservice.feignclient;

import com.example.tccpayment.entity.ReservedPayment;

public interface PaymentServiceClientService {
    ReservedPayment reservedPayment(String productList, String orderSerial);

    Boolean confirmPayment(Long id);

    void cancelOrder(Long id);
}
