package com.team6.onandthefarmpaymentservice.feignclient;

import com.team6.onandthefarmpaymentservice.feignclient.vo.OrderVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "order-service")
public interface OrderServiceClient {
    @GetMapping("/api/feign/user/orders/order-service/{order-serial}")
    OrderVo findByOrderSerial(@PathVariable("order-serial") String orderSerial);
}
