package com.team6.onandthefarmpaymentservice.feignclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team6.onandthefarmpaymentservice.dto.OrderProductDto;
import com.team6.onandthefarmpaymentservice.dto.PaymentApiDto;
import com.team6.onandthefarmpaymentservice.dto.PaymentDto;
import com.team6.onandthefarmpaymentservice.entity.ReservedPayment;
import com.team6.onandthefarmpaymentservice.kafka.PaymentOrderChannelAdapter;
import com.team6.onandthefarmpaymentservice.kafka.vo.*;
import com.team6.onandthefarmpaymentservice.repository.ReservedPaymentRepository;
import com.team6.onandthefarmpaymentservice.service.PaymentService;
import com.team6.onandthefarmpaymentservice.util.PaymentUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class PaymentServiceClientServiceImpl implements PaymentServiceClientService{

    private final ReservedPaymentRepository reservedPaymentRepository;

    private final PaymentOrderChannelAdapter paymentOrderChannelAdapter;

    private final PaymentService paymentService;

    private final KafkaTemplate<String,String> kafkaTemplate;

    private final PaymentUtils paymentUtils;

    private final String topic = "reserved_payment_sink";

    List<Field> fields = Arrays.asList(new Field("int64",false,"reserved_payment_id"),
            new Field("string",true,"created_date"),
            new Field("string",true,"expire_time"),
            new Field("string",true,"imp_uid"),
            new Field("string",true,"merchant_uid"),
            new Field("string",true,"order_serial"),
            new Field("string",true,"paid_amount"),
            new Field("string",true,"product_list"),
            new Field("string",true,"status"));
    Schema schema = Schema.builder()
            .type("struct")
            .fields(fields)
            .optional(false)
            .name("reserved_payment")
            .build();


    /**
     * 주문 예약 테이블에 주문 정보를 db에 저장하는 메서드(try)
     * @param productList
     * @return
     */
    public Payload reservedPayment(String productList, PaymentApiDto paymentApiDto) {
        Payload payload = Payload.builder()
                .reserved_payment_id(Long.valueOf(new Date().getTime()))
                .created_date(LocalDateTime.now().toString())
                .expire_time(LocalDateTime.now().plus(100l, ChronoUnit.SECONDS).toString())
                .order_serial(paymentApiDto.getOrderSerial())
                .product_list(productList)
                .imp_uid(paymentApiDto.getImp_uid())
                .merchant_uid(paymentApiDto.getMerchant_uid())
                .paid_amount(paymentApiDto.getPaid_amount())
                .build();
        KafkaPaymentDto kafkaPaymentDto = new KafkaPaymentDto(schema,payload);
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = "";
        try{
            jsonInString = mapper.writeValueAsString(kafkaPaymentDto);
        }catch(JsonProcessingException ex){
            ex.printStackTrace();
        }

        kafkaTemplate.send(topic,jsonInString);
        return payload;
    }

    /**
     * 주문 확정(confirm)을 처리하는 메서드
     * @param id : 예약 테이블에 저장된 예약된 주문 정보의 pk값
     * @return
     */
    public Boolean confirmPayment(Long id) throws IOException {
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
        PaymentApiDto paymentDto = PaymentApiDto.builder()
                .orderSerial(reservedPayment.getOrderSerial())
                .paid_amount(reservedPayment.getPaid_amount())
                .merchant_uid(reservedPayment.getMerchant_uid())
                .imp_uid(reservedPayment.getImp_uid())
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        String message = ""; // confirm 메시지
        try{
            message = objectMapper.writeValueAsString(paymentDto); // 결제 생성
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        String token = paymentUtils.getToken(); // JWT 토큰 가져오기

        //System.out.println("토큰 : " + token);
        // 결제 완료된 금액
        int amount = paymentUtils.paymentInfo(paymentDto.getImp_uid(), token);
        if(paymentDto.getPaid_amount().equals(String.valueOf(amount))){ // 검증
            paymentOrderChannelAdapter.producer(message);
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public void cancelOrder(Long id) {
        ReservedPayment reservedPayment = reservedPaymentRepository.findById(id).get();
        reservedPayment.setStatus("CANCEL");
        log.info("Cancel Stock :" + id);
    }
}
