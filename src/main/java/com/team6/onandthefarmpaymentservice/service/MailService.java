package com.team6.onandthefarmpaymentservice.service;


import java.util.Map;


public interface MailService {

    void sendAuthMail(String orderSerial,Integer price);
}
