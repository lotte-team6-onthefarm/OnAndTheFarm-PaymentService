package com.team6.onandthefarmpaymentservice.kafka;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.team6.onandthefarmpaymentservice.dto.DlqPaymentDto;
import com.team6.onandthefarmpaymentservice.dto.PaymentApiDto;
import com.team6.onandthefarmpaymentservice.dto.PaymentDto;
import com.team6.onandthefarmpaymentservice.service.PaymentService;
import com.team6.onandthefarmpaymentservice.vo.PaymentVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentOrderChannelAdapterKafkaImpl implements PaymentOrderChannelAdapter{
    private final String TOPIC = "payment-order";

    private final String DLQ_TOPIC = "dlt-payment-order";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final PaymentService paymentService;


    public void producer(String message) {
        this.kafkaTemplate.send(TOPIC, message);
    }

    @KafkaListener(topics = TOPIC,containerFactory = "kafkaListenerContainerFactory")
    public void consumer(String message, Acknowledgment ack) throws Exception {
        log.info(String.format("Message Received : %s", message));
        ObjectMapper objectMapper = new ObjectMapper();
        PaymentApiDto paymentDto = objectMapper.readValue(message, PaymentApiDto.class);
        if(paymentService.isAlreadyProcessedOrderId(paymentDto.getOrderSerial())){
            // 중복되지 않은 메시지임으로 결제 생성
            paymentService.createPayment(paymentDto);
        }

        Long test = Long.valueOf("adsasd");

        // Kafka Offset Manual Commit(수동커밋)
        ack.acknowledge();
    }

    @KafkaListener(topics = DLQ_TOPIC,containerFactory = "kafkaListenerContainerFactory")
    public void dlq_consumer(String message, Acknowledgment ack) throws Exception {
        log.info(String.format("Dead-Message Received : %s", message));
        ObjectMapper objectMapper = new ObjectMapper();
        DlqPaymentDto paymentDto = objectMapper.readValue(message, DlqPaymentDto.class);
        PaymentVo paymentVo = PaymentVo.builder()
                .imp_uid(paymentDto.getImp_uid())
                .merchant_uid(paymentDto.getMerchant_uid())
                .paid_amount(paymentDto.getPaid_amount())
                .build();
        paymentService.cancelPayment(paymentVo);

        paymentService.createDlqPayment(paymentDto);

        // Kafka Offset Manual Commit(수동커밋)
        ack.acknowledge();
    }
}
