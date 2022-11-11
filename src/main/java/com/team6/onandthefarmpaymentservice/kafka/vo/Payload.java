package com.team6.onandthefarmpaymentservice.kafka.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Payload {
    private Long reserved_payment_id;

    private String created_date;

    private String expire_time;

    private String imp_uid;

    private String merchant_uid;

    private String order_serial;

    private String paid_amount;

    private String product_list;

    private String status;
}
