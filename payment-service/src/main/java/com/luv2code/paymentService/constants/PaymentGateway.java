package com.luv2code.paymentService.constants;

public enum PaymentGateway {

    PhonePe(1);

    private final int gatewayId;
    PaymentGateway(int gatewayId) {
        this.gatewayId = gatewayId;
    }

    public int getGatewayId(){
        return this.gatewayId;
    }
}
