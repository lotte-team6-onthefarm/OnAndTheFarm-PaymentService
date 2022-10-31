package com.team6.onandthefarmpaymentservice.kafka;

import org.springframework.kafka.support.Acknowledgment;

import java.io.IOException;

public interface PaymentOrderChannelAdapter {
    void producer(String message);

    void consumer(String message, Acknowledgment ack) throws Exception;
}
