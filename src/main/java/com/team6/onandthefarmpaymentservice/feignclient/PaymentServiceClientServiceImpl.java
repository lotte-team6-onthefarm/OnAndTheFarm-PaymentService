package com.team6.onandthefarmpaymentservice.feignclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team6.onandthefarmpaymentservice.dto.OrderProductDto;
import com.team6.onandthefarmpaymentservice.dto.PaymentDto;
import com.team6.onandthefarmpaymentservice.entity.ReservedPayment;
import com.team6.onandthefarmpaymentservice.kafka.PaymentOrderChannelAdapter;
import com.team6.onandthefarmpaymentservice.repository.ReservedPaymentRepository;
import com.team6.onandthefarmpaymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class PaymentServiceClientServiceImpl implements PaymentServiceClientService{

    private final ReservedPaymentRepository reservedPaymentRepository;

    private final PaymentOrderChannelAdapter paymentOrderChannelAdapter;

    private final PaymentService paymentService;


    /**
     * 주문 예약 테이블에 주문 정보를 db에 저장하는 메서드(try)
     * @param productList
     * @return
     */
    public ReservedPayment reservedPayment(String productList, String orderSerial) {
        ReservedPayment reservedPayment = ReservedPayment.builder()
                .productList(productList)
                .createdDate(LocalDateTime.now())
                .expireTime(LocalDateTime.now().plus(3l, ChronoUnit.SECONDS))
                .orderSerial(orderSerial)
                .build();
        return reservedPaymentRepository.save(reservedPayment);
    }

    /**
     * 주문 확정(confirm)을 처리하는 메서드
     * @param id : 예약 테이블에 저장된 예약된 주문 정보의 pk값
     * @return
     */
    public Boolean confirmPayment(Long id) {
        ReservedPayment reservedPayment = reservedPaymentRepository.findById(id).get();
        reservedPayment.validate(); // 예약 정보의 유효성 검증

        List<OrderProductDto> orderProductDtoList = new ArrayList<>();
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            orderProductDtoList
                    = objectMapper.readValue(reservedPayment.getProductList(), List.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Boolean.FALSE;
        }

        int totalPrice = 0; // 총 결제 금액

        for(Object orderProduct : orderProductDtoList){
            HashMap<String,Object> orderProductDto = (HashMap<String, Object>) orderProduct;
            Long productId = Long.valueOf(String.valueOf(orderProductDto.get("productId")));

            /**
             *  결제 관련 코드가 들어가야함
             *  현재는 간단하게 결제 insert만 함
             */
            totalPrice += Integer.valueOf(String.valueOf(orderProductDto.get("productPrice")));

        }
        // ReservedStock 상태 변경
        reservedPayment.setStatus("CONFIRMED");
        // 이후 confirm 메시지를 생성 및 전송
        PaymentDto paymentDto = PaymentDto.builder()
                .orderSerial(reservedPayment.getOrderSerial())
                .paymentDepositAmount(totalPrice)
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        String message = ""; // confirm 메시지
        try{
            message = objectMapper.writeValueAsString(paymentDto); // 결제 생성
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        paymentOrderChannelAdapter.producer(message);
        return Boolean.TRUE;
    }

    public void cancelOrder(Long id) {
        ReservedPayment reservedPayment = reservedPaymentRepository.findById(id).get();
        reservedPayment.setStatus("CANCEL");
        log.info("Cancel Stock :" + id);
    }
}
