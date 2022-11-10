package com.team6.onandthefarmpaymentservice.feignclient;


import com.team6.onandthefarmpaymentservice.dto.PaymentApiDto;
import com.team6.onandthefarmpaymentservice.entity.ReservedPayment;
import com.team6.onandthefarmpaymentservice.kafka.vo.Payload;

public interface PaymentServiceClientService {
    Payload reservedPayment(String productList, PaymentApiDto paymentApiDto);

    Boolean confirmPayment(Long id);

    void cancelOrder(Long id);
}
