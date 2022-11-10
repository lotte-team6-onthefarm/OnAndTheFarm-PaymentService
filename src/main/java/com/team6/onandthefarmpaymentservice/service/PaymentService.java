package com.team6.onandthefarmpaymentservice.service;


import com.team6.onandthefarmpaymentservice.dto.PaymentApiDto;
import com.team6.onandthefarmpaymentservice.dto.PaymentDto;
import com.team6.onandthefarmpaymentservice.entity.Payment;
import com.team6.onandthefarmpaymentservice.vo.PaymentVo;

import java.io.IOException;

public interface PaymentService {
    Payment createPayment(PaymentApiDto paymentDto) throws IOException;

    Boolean isAlreadyProcessedOrderId(String orderSerial);

    Boolean createDlqPayment(PaymentDto paymentDto);

    void cancelPayment(PaymentVo paymentVo) throws IOException;
}
