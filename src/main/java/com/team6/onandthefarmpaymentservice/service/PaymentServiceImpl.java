package com.team6.onandthefarmpaymentservice.service;

import com.example.tccpayment.dto.PaymentDto;
import com.example.tccpayment.entity.Payment;
import com.example.tccpayment.repository.PaymentRepository;
import com.example.tccpayment.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService{

    private final PaymentRepository paymentRepository;

    private final DateUtils dateUtils;

    public Payment createPayment(PaymentDto paymentDto){
        Payment payment = Payment.builder()
                .orderSerial(paymentDto.getOrderSerial())
                .paymentDate(dateUtils.transDate("yyyy.MM.dd HH:mm:ss"))
                .paymentDepositAmount(paymentDto.getPaymentDepositAmount())
                .build();
        return paymentRepository.save(payment);
    }

    /**
     * 메시지 중복을 해결하기 위한 메서드
     * @param orderSerial
     * @return true : 중복되지 않은 메시지 / false : 중복된 메시지
     */
    public Boolean isAlreadyProcessedOrderId(String orderSerial){
        Optional<Payment> payment = paymentRepository.findByOrderSerial(orderSerial);

        if(payment.isPresent()){ // 이미 처리된 메시지라는 의미(중복이라는 의미)
            return Boolean.FALSE;
        }
        return Boolean.TRUE; // 중복되지 않은 메시지라는 의미
    }
}
