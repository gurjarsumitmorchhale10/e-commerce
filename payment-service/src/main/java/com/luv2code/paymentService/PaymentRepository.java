package com.luv2code.paymentService;

import com.luv2code.paymentService.entity.PaymentDetail;
import org.springframework.data.repository.CrudRepository;

public interface PaymentRepository extends CrudRepository<PaymentDetail,Long> {
}
