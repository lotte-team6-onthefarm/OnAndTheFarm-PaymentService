package com.team6.onandthefarmpaymentservice.kafka;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.team6.onandthefarmpaymentservice.dto.PaymentDto;
import com.team6.onandthefarmpaymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PaymentOrderChannelAdapterKafkaImpl implements PaymentOrderChannelAdapter{
    private final String TOPIC = "payment-order";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final PaymentService paymentService;


    public void producer(String message) {
        this.kafkaTemplate.send(TOPIC, message);
    }

    @KafkaListener(topics = TOPIC)
    public void consumer(String message, Acknowledgment ack) {
        log.info(String.format("Message Received : %s", message));
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            PaymentDto paymentDto = objectMapper.readValue(message, PaymentDto.class);
            if(paymentService.isAlreadyProcessedOrderId(paymentDto.getOrderSerial())){
                // 중복되지 않은 메시지임으로 결제 생성
                paymentService.createPayment(paymentDto);
            }

            // Kafka Offset Manual Commit(수동커밋)
            ack.acknowledge();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
