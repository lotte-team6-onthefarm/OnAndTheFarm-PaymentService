package com.team6.onandthefarmpaymentservice.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {

    private String orderSerial;

    private String paymentMethod;

    private Integer paymentDepositAmount;

    private String paymentDepositName;

    private String paymentDepositBank;

    private String paymentRefundAccount;

    private String paymentRefundAccountName;
}
