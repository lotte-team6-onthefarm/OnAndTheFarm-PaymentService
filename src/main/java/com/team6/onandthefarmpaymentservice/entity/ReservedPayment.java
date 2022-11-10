package com.team6.onandthefarmpaymentservice.entity;

import com.thoughtworks.xstream.converters.time.LocalDateTimeConverter;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;

@Builder
@Slf4j
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReservedPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long reservedPaymentId;

    private String productList;

    private String orderSerial;

    private String createdDate;

    private String expireTime;

    private String status;

    private String imp_uid;

    private String merchant_uid;

    private String paid_amount;

    public void validate() {
        validateStatus();
        validateExpired();
    }
    private void validateStatus() {
        if(this.getStatus()==null) return;
        if(this.getStatus().equals("CANCEL") || this.getStatus().equals("CONFIRMED")) {
            throw new IllegalArgumentException("Invalidate Status");
        }
    }
    private void validateExpired() {
        Integer year = Integer.valueOf(this.expireTime.substring(0,4));
        Integer month = Integer.valueOf(this.expireTime.substring(5,7));
        Integer day = Integer.valueOf(this.expireTime.substring(8,10));
        Integer hh = Integer.valueOf(this.expireTime.substring(11,13));
        Integer mm = Integer.valueOf(this.expireTime.substring(14,16));
        Integer ss = Integer.valueOf(this.expireTime.substring(17,19));

        if(LocalDateTime.now().isAfter(LocalDateTime.of(year,month,day,hh,mm,ss))) {
            throw new IllegalArgumentException("Expired");
        }
    }
}
