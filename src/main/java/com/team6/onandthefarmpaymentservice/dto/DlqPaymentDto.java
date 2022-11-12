package com.team6.onandthefarmpaymentservice.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DlqPaymentDto {
    private String orderSerial;

    private String imp_uid;

    private String merchant_uid;

    private String paid_amount;
}
