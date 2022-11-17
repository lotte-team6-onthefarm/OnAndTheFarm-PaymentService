package com.team6.onandthefarmpaymentservice.feignclient;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team6.onandthefarmpaymentservice.ParticipantLink;
import com.team6.onandthefarmpaymentservice.dto.PaymentApiDto;
import com.team6.onandthefarmpaymentservice.entity.ReservedPayment;
import com.team6.onandthefarmpaymentservice.kafka.vo.Payload;

import com.team6.onandthefarmpaymentservice.service.PaymentService;
import com.team6.onandthefarmpaymentservice.util.BaseResponse;
import com.team6.onandthefarmpaymentservice.vo.DlqPaymentResponse;
import com.team6.onandthefarmpaymentservice.vo.PaymentVo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PaymentServiceClientController {

    private final PaymentServiceClientService paymentServiceClientService;

    private final PaymentService paymentService;

    @PostMapping("/api/feign/user/payment/payment-service/payment-try")
    public ResponseEntity<ParticipantLink> paymentTry(@RequestBody Map<String, Object> map){
        String productList = "";
        String orderSerial = "";
        String imp_uid = "";
        String merchant_uid = "";
        String paid_amount = "";


        ObjectMapper objectMapper = new ObjectMapper();

        PaymentApiDto paymentApiDto = null;
        try{
            // 상품들의 정보를 직렬화
            productList = objectMapper.writeValueAsString(map.get("productIdList"));

            orderSerial = objectMapper.writeValueAsString(map.get("orderSerial"));
            orderSerial = orderSerial.substring(1,orderSerial.length()-1);

            imp_uid = objectMapper.writeValueAsString(map.get("imp_uid"));
            imp_uid = imp_uid.substring(1,imp_uid.length()-1);

            merchant_uid = objectMapper.writeValueAsString(map.get("merchant_uid"));
            merchant_uid = merchant_uid.substring(1,merchant_uid.length()-1);

            merchant_uid = objectMapper.writeValueAsString(map.get("merchant_uid"));
            merchant_uid = merchant_uid.substring(1,merchant_uid.length()-1);

            paid_amount = objectMapper.writeValueAsString(map.get("paid_amount"));
            paid_amount = paid_amount.substring(1,paid_amount.length()-1);

            paymentApiDto = PaymentApiDto.builder()
                    .imp_uid(imp_uid)
                    .merchant_uid(merchant_uid)
                    .orderSerial(orderSerial)
                    .paid_amount(paid_amount)
                    .build();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        // 주문 예약 테이블에 예약 저장
        Payload reservedPayment = paymentServiceClientService.reservedPayment(productList,paymentApiDto);

        final ParticipantLink participantLink = buildParticipantLink(
                reservedPayment.getReserved_payment_id(),
                reservedPayment.getExpire_time());
        return new ResponseEntity<>(participantLink, HttpStatus.CREATED);
    }

    @PutMapping("/api/feign/user/payment/payment-service/payment-try/{id}")
    public ResponseEntity<Void> confirmPaymentAdjustment(@PathVariable Long id) {
        try {
            paymentServiceClientService.confirmPayment(id);
        } catch(IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IOException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private ParticipantLink buildParticipantLink(final Long id, String expire) {
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();
        return new ParticipantLink(location, expire);
    }

    @DeleteMapping("/api/feign/user/payment/payment-service/payment-try/{id}")
    public ResponseEntity<Void> cancelOrderAdjustment(@PathVariable Long id) {
        paymentServiceClientService.cancelOrder(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/api/feign/payment-service/cancel")
    public Boolean cancelPayment(@RequestBody PaymentVo paymentVo) {
        try {
            paymentService.cancelPayment(paymentVo);
        }catch (IOException e){
            e.printStackTrace();
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    @GetMapping("/api/admin/payment-service/dlt-payment")
    public ResponseEntity<BaseResponse<List<DlqPaymentResponse>>> findDltPayment(){
        List<DlqPaymentResponse> dlqPaymentResponses = paymentService.findDltPayment();

        BaseResponse baseResponse = BaseResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("OK")
                .data(dlqPaymentResponses)
                .build();
        return new ResponseEntity(baseResponse,HttpStatus.OK);
    }
}
