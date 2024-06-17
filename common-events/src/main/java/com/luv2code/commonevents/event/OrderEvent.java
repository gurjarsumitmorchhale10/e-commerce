package com.luv2code.commonevents.event;

import com.luv2code.commonevents.dto.OrderDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class OrderEvent implements Event{

    private UUID eventId;
    private LocalDateTime eventTime;

    OrderStatus orderStatus;
    OrderDto orderDto;

    public OrderEvent(OrderStatus orderStatus, OrderDto orderDto) {
        this.orderStatus = orderStatus;
        this.orderDto = orderDto;
    }

    @Override
    public UUID getEventID() {
        return eventId;
    }

    @Override
    public LocalDateTime getEventTime() {
        return eventTime;
    }
}
