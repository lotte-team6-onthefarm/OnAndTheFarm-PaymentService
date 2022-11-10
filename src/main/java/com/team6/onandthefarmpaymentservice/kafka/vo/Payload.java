package com.team6.onandthefarmpaymentservice.kafka.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Payload {
    private Long reservedPaymentId;

    private String productList;

    private String orderSerial;

    private String createdDate;

    private String expireTime;

    private String status;

    private String imp_uid;

    private String merchant_uid;

    private String paid_amount;
}
