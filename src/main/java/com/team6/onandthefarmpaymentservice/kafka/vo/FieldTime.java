package com.team6.onandthefarmpaymentservice.kafka.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FieldTime {
    private String type;

    private boolean optional;

    private String name;

    private Integer version;

    private String field;
}
