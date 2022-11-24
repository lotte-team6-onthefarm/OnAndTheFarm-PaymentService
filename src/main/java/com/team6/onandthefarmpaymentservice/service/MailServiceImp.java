package com.team6.onandthefarmpaymentservice.service;


import com.team6.onandthefarmpaymentservice.feignclient.OrderServiceClient;
import com.team6.onandthefarmpaymentservice.feignclient.UserServiceClient;
import com.team6.onandthefarmpaymentservice.feignclient.vo.OrderVo;
import com.team6.onandthefarmpaymentservice.feignclient.vo.UserVo;
import com.team6.onandthefarmpaymentservice.util.MailUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MailServiceImp implements MailService{

    private final UserServiceClient userServiceClient;

    private final OrderServiceClient orderServiceClient;

    private JavaMailSenderImpl mailSender;


    //인증메일 보내기
    @Override
    public void sendAuthMail(String orderSerial,Integer price) {
        // orderSerial로 주문자 email 찾기
        // 주문 가져오기
        OrderVo order = orderServiceClient.findByOrderSerial(orderSerial);
        // 유저 가져오기
        UserVo user = userServiceClient.findByUserId(order.getUserId());
        // email 가져오기
        String email = user.getUserEmail();

        //인증메일 보내기
        try {
            MailUtils sendMail = new MailUtils(mailSender);
            sendMail.setSubject("죄송합니다. 결제가 취소되었습니다. 다시 결제를 시도 해주세요.");
            sendMail.setText(new StringBuffer().append("<h1>[결제 취소에 대한 재결제]</h1>")
                    .append("<p>아래 링크를 클릭하시면 결제 페이지로 이동됩니다.</p>")
                    .append("localhost:3000/repayment?orderserial="+orderSerial+"&price="+price)
                    .append("<p>주문번호 : "+orderSerial+"</p>")
                    .append("<p>결제금액 : "+price+"</p>")
                    .append("<h1>[재결제가 완료되면 배송이 시작됩니다]</h1>")
                    .toString());
            sendMail.setFrom("ksh9409255@gmail.com", "관리자"); // 관리자 email 작성해주고 관리자 email의 경우 2차비밀번호 인증을 해야함
            sendMail.setTo(email);
            sendMail.send();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
