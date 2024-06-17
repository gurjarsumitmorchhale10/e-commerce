package com.luv2code.paymentService.config;

import com.phonepe.sdk.pg.Env;
import com.phonepe.sdk.pg.payments.v1.PhonePePaymentClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PhonePePaymentGatewayConfiguration {

    @Value("${phonePe.merchantId}")
    private String merchantId;

    @Value("${phonePe.saltKey}")
    private String saltKey;

    @Value("${phonePe.saltIndex}")
    private Integer saltIndex;


    private final Env env = Env.SIMULATOR;

    private final boolean shouldPublishEvents = true;

    @Bean
    public PhonePePaymentClient phonePePaymentClient() {
        return new PhonePePaymentClient(merchantId, saltKey, saltIndex, env, shouldPublishEvents);
    }
}
