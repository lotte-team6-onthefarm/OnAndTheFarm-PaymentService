package com.team6.onandthefarmpaymentservice.feignclient;


import com.team6.onandthefarmpaymentservice.dto.PaymentApiDto;
import com.team6.onandthefarmpaymentservice.entity.ReservedPayment;

public interface PaymentServiceClientService {
    ReservedPayment reservedPayment(String productList, PaymentApiDto paymentApiDto);

    Boolean confirmPayment(Long id);

    void cancelOrder(Long id);
}
