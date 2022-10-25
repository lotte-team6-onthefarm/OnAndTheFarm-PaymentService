package com.team6.onandthefarmpaymentservice.kafka;

import org.springframework.kafka.support.Acknowledgment;

public interface PaymentOrderChannelAdapter {
    void producer(String message);

    void consumer(String message, Acknowledgment ack);
}
