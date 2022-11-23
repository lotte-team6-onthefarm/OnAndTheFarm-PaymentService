package com.team6.onandthefarmpaymentservice.feignclient;

import com.team6.onandthefarmpaymentservice.feignclient.vo.UserVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "member-service")
public interface UserServiceClient {
	@GetMapping("/api/feign/user/members/member-service/{user-no}")
	UserVo findByUserId(@PathVariable("user-no") Long userId);
}
