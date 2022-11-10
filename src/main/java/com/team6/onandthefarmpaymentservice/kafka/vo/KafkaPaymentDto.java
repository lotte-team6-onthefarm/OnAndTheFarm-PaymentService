package com.team6.onandthefarmpaymentservice.kafka.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class KafkaPaymentDto implements Serializable {
    private Schema schema;
    private Payload payload;
}
