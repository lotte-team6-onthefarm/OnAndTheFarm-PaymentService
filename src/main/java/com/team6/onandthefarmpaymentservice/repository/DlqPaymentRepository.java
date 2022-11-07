package com.team6.onandthefarmpaymentservice.repository;

import com.team6.onandthefarmpaymentservice.entity.DlqPayment;
import com.team6.onandthefarmpaymentservice.entity.ReservedPayment;
import org.springframework.data.repository.CrudRepository;

public interface DlqPaymentRepository extends CrudRepository<DlqPayment,Long> {
}
