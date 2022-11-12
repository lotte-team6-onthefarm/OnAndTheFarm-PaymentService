package com.team6.onandthefarmpaymentservice.vo;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DlqPaymentResponse {
    private Long dlqPaymentId;

    private String orderSerial;

    private Integer paymentDepositAmount;
}
