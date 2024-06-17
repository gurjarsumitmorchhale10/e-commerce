package com.luv2code.paymentService.entity;

import com.luv2code.paymentService.constants.PaymentGateway;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payment_response")
@RequiredArgsConstructor
@Getter
@Setter
@Builder
public class PaymentDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String transactionId;
    private boolean success;
    private long amount;
    private String username;
    private String orderId;
    private PaymentGateway paymentGateway;
    private String message;
}

