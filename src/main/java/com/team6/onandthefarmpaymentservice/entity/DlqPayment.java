package com.team6.onandthefarmpaymentservice.entity;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Builder
@Slf4j
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DlqPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long dlqPaymentId;

    private String orderSerial;

    private String imp_uid;

    private String merchant_uid;

    private String paid_amount;
}
