package com.team6.onandthefarmpaymentservice.feignclient;


import com.team6.onandthefarmpaymentservice.dto.PaymentApiDto;
import com.team6.onandthefarmpaymentservice.entity.ReservedPayment;
import com.team6.onandthefarmpaymentservice.kafka.vo.Payload;

import java.io.IOException;

public interface PaymentServiceClientService {
    Payload reservedPayment(String productList, PaymentApiDto paymentApiDto);

    Boolean confirmPayment(Long id) throws IOException;

    void cancelOrder(Long id);
}
