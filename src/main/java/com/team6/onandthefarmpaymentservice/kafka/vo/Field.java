package com.team6.onandthefarmpaymentservice.kafka.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Field {
    private String type;

    private boolean optional;

    private String field;
}
