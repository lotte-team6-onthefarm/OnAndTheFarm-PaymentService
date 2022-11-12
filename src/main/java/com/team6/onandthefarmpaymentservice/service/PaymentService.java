package com.team6.onandthefarmpaymentservice.service;


import com.team6.onandthefarmpaymentservice.dto.DlqPaymentDto;
import com.team6.onandthefarmpaymentservice.dto.PaymentApiDto;
import com.team6.onandthefarmpaymentservice.dto.PaymentDto;
import com.team6.onandthefarmpaymentservice.entity.Payment;
import com.team6.onandthefarmpaymentservice.vo.DlqPaymentResponse;
import com.team6.onandthefarmpaymentservice.vo.PaymentVo;

import java.io.IOException;
import java.util.List;

public interface PaymentService {
    Payment createPayment(PaymentApiDto paymentDto) throws IOException;

    Boolean isAlreadyProcessedOrderId(String orderSerial);

    Boolean createDlqPayment(DlqPaymentDto paymentDto);

    void cancelPayment(PaymentVo paymentVo) throws IOException;

    List<DlqPaymentResponse> findDltPayment();
}
