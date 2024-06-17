package com.luv2code.commonevents.dto;

import lombok.*;

import java.util.List;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public final class OrderDto {

    private Long orderId;
    private String orderStatus;
    private String orderNumber;
    private List<OrderLineItemDto> orderLineItems;

    public Long orderId() {
        return orderId;
    }

    public List<OrderLineItemDto> orderLineItems() {
        return orderLineItems;
    }

}