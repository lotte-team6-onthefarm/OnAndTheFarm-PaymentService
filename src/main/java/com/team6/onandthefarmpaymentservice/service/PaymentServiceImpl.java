package com.team6.onandthefarmpaymentservice.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.team6.onandthefarmpaymentservice.dto.DlqPaymentDto;
import com.team6.onandthefarmpaymentservice.dto.PaymentApiDto;
import com.team6.onandthefarmpaymentservice.dto.PaymentDto;
import com.team6.onandthefarmpaymentservice.entity.DlqPayment;
import com.team6.onandthefarmpaymentservice.entity.Payment;
import com.team6.onandthefarmpaymentservice.repository.DlqPaymentRepository;
import com.team6.onandthefarmpaymentservice.repository.PaymentRepository;
import com.team6.onandthefarmpaymentservice.util.DateUtils;
import com.team6.onandthefarmpaymentservice.util.PaymentUtils;
import com.team6.onandthefarmpaymentservice.vo.DlqPaymentResponse;
import com.team6.onandthefarmpaymentservice.vo.PaymentVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.util.*;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService{

    private final PaymentRepository paymentRepository;

    private final DlqPaymentRepository dlqPaymentRepository;

    private final DateUtils dateUtils;

    private final PaymentUtils paymentUtils;

    public Payment createPayment(PaymentApiDto paymentDto) throws IOException {
        if(paymentDto.getPaid_amount().equals("2")){
            Long test = Long.valueOf("adsasd");
        }
        Payment payment = Payment.builder()
                .orderSerial(paymentDto.getOrderSerial())
                .paymentDate(dateUtils.transDate("yyyy.MM.dd HH:mm:ss"))
                .paymentDepositAmount(Integer.valueOf(paymentDto.getPaid_amount()))
                .build();
        return paymentRepository.save(payment);
    }

    /**
     * 메시지 중복을 해결하기 위한 메서드
     * @param orderSerial
     * @return true : 중복되지 않은 메시지 / false : 중복된 메시지
     */
    public Boolean isAlreadyProcessedOrderId(String orderSerial){
        //List<Payment> payment = paymentRepository.findByOrderSerial(orderSerial);

        boolean result = paymentRepository.existsByOrderSerial(orderSerial); // true : 존재 / false : 미존재
        if(result){ // 이미 처리된 메시지라는 의미(중복이라는 의미)
            return Boolean.FALSE;
        }
        return Boolean.TRUE; // 중복되지 않은 메시지라는 의미
    }

    @Override
    public Boolean createDlqPayment(DlqPaymentDto paymentDto) {
        DlqPayment dlqPayment = DlqPayment.builder()
                .orderSerial(paymentDto.getOrderSerial())
                .imp_uid(paymentDto.getImp_uid())
                .merchant_uid(paymentDto.getMerchant_uid())
                .paid_amount(paymentDto.getPaid_amount())
                .build();

        DlqPayment saveDlqPayment = dlqPaymentRepository.save(dlqPayment);

        if(saveDlqPayment==null){
            return false;
        }

        return true;
    }

    @Override
    public void cancelPayment(PaymentVo paymentVo) throws IOException {
        String token = paymentUtils.getToken(); // JWT 토큰 가져오기

        System.out.println("토큰 : " + token);
        // 결제 완료된 금액
        int amount = paymentUtils.paymentInfo(paymentVo.getImp_uid(), token);
        paymentUtils.payMentCancle(token,paymentVo.getImp_uid(),amount,"결제 취소");
    }

    /**
     * dlt-payment-order 에 저장된 메시지를 가져오는 메서드
     */
    @Override
    public List<DlqPaymentResponse> findDltPayment() {
        List<DlqPaymentResponse> responses = new ArrayList<>();

        Iterator<DlqPayment> dlqPaymentIterator = dlqPaymentRepository.findAll().iterator();
        while(true){
            if(dlqPaymentIterator.hasNext()){
                DlqPayment dlqPayment = dlqPaymentIterator.next();

                DlqPaymentResponse response = DlqPaymentResponse.builder()
                        .dlqPaymentId(dlqPayment.getDlqPaymentId())
                        .orderSerial(dlqPayment.getOrderSerial())
                        .paymentDepositAmount(Integer.valueOf(dlqPayment.getPaid_amount()))
                        .build();

                responses.add(response);
            }
            else{
                break;
            }
        }

        return responses;
    }
}
