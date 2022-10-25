package com.team6.onandthefarmpaymentservice.repository;



import com.team6.onandthefarmpaymentservice.entity.Payment;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PaymentRepository extends CrudRepository<Payment,Long> {
    Optional<Payment> findByOrderSerial(String orderSerial);
}
