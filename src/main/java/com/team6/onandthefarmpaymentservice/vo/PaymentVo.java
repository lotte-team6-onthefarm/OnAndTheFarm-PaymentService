package com.team6.onandthefarmpaymentservice.vo;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentVo {
    private String imp_uid;

    private String merchant_uid;

    private String  paid_amount;
}
