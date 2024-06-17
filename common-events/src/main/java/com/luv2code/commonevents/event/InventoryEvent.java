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
public class InventoryEvent implements Event {


    private UUID eventId;
    private LocalDateTime eventTime;

    private OrderDto orderDto;
    private InventoryStatus inventoryStatus;

    public InventoryEvent(OrderDto orderDto, InventoryStatus inventoryStatus) {
        this.orderDto = orderDto;
        this.inventoryStatus = inventoryStatus;
    }

    @Override
    public UUID getEventID() {
        return null;
    }

    @Override
    public LocalDateTime getEventTime() {
        return null;
    }
}
