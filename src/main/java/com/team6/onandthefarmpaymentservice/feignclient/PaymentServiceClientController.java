package com.team6.onandthefarmpaymentservice.feignclient;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team6.onandthefarmpaymentservice.ParticipantLink;
import com.team6.onandthefarmpaymentservice.entity.ReservedPayment;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PaymentServiceClientController {

    private final PaymentServiceClientService paymentServiceClientService;

    @PostMapping("/api/user/payment/payment-service/payment-try")
    public ResponseEntity<ParticipantLink> paymentTry(@RequestBody Map<String, Object> map){
        String productList = "";
        String orderSerial = "";


        ObjectMapper objectMapper = new ObjectMapper();
        try{
            // 상품들의 정보를 직렬화
            productList = objectMapper.writeValueAsString(map.get("productIdList"));
            orderSerial = objectMapper.writeValueAsString(map.get("orderSerial"));
            orderSerial = orderSerial.substring(1,orderSerial.length()-1);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        // 주문 예약 테이블에 예약 저장
        ReservedPayment reservedPayment = paymentServiceClientService.reservedPayment(productList,orderSerial);

        final ParticipantLink participantLink = buildParticipantLink(
                reservedPayment.getReservedPaymentId(),
                reservedPayment.getExpireTime());
        return new ResponseEntity<>(participantLink, HttpStatus.CREATED);
    }

    @PutMapping("/api/user/payment/payment-service/payment-try/{id}")
    public ResponseEntity<Void> confirmPaymentAdjustment(@PathVariable Long id) {
        try {
            paymentServiceClientService.confirmPayment(id);
        } catch(IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private ParticipantLink buildParticipantLink(final Long id, LocalDateTime expire) {
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();
        return new ParticipantLink(location, expire);
    }

    @DeleteMapping("/api/user/payment/payment-service/payment-try/{id}")
    public ResponseEntity<Void> cancelOrderAdjustment(@PathVariable Long id) {
        paymentServiceClientService.cancelOrder(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
