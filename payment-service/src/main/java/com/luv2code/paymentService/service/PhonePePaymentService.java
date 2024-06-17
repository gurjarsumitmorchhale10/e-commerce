package com.luv2code.paymentService.service;

import com.luv2code.paymentService.PaymentRepository;
import com.luv2code.paymentService.constants.PaymentGateway;
import com.luv2code.paymentService.entity.PaymentDetail;
import com.phonepe.sdk.pg.common.http.PhonePeResponse;
import com.phonepe.sdk.pg.payments.v1.PhonePePaymentClient;
import com.phonepe.sdk.pg.payments.v1.models.request.PgPayRequest;
import com.phonepe.sdk.pg.payments.v1.models.response.PayPageInstrumentResponse;
import com.phonepe.sdk.pg.payments.v1.models.response.PgPayResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PhonePePaymentService {

    private final String merchantId="merchantId";
    private final String merchantUserId="merchantUserId";
    private final String callbackurl="https://www.merchant.com/callback";
    private final String redirecturl="https://www.merchant.com/redirect";
    private final String redirectMode="REDIRECT";

    private final PhonePePaymentClient phonePePaymentClient;
    private final PaymentRepository paymentRepository;

    public void initiatePayment(long amount){

        String merchantTransactionId = UUID.randomUUID().toString().substring(0,34);

        PgPayRequest pgPayRequest=PgPayRequest.PayPagePayRequestBuilder()
                .amount(amount)
                .merchantId(merchantId)
                .merchantTransactionId(merchantTransactionId)
                .callbackUrl(callbackurl)
                .merchantUserId(merchantUserId)
                .redirectUrl(redirecturl)
                .redirectMode(redirectMode)
                .build();


        PhonePeResponse<PgPayResponse> payResponse=phonePePaymentClient.pay(pgPayRequest);
        PayPageInstrumentResponse payPageInstrumentResponse=(PayPageInstrumentResponse)payResponse.getData().getInstrumentResponse();
        String url=payPageInstrumentResponse.getRedirectInfo().getUrl();

    }

    private void savePhonePePaymentStatus(long amount, PhonePeResponse<PgPayResponse> payResponse) {
        PaymentDetail paymentDetail = PaymentDetail.builder()
                .paymentGateway(PaymentGateway.PhonePe)
                .amount(amount)
                .orderId("")
                .success(payResponse.getSuccess())
                .transactionId(payResponse.getData().getTransactionId())
                .message(payResponse.getMessage())
                .username("")
                .build();

        paymentRepository.save(paymentDetail);
    }
}
