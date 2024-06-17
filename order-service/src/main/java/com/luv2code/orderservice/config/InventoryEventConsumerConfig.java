package com.luv2code.orderservice.config;

import com.luv2code.commonevents.event.InventoryEvent;
import com.luv2code.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;


@Configuration
@RequiredArgsConstructor
public class InventoryEventConsumerConfig {

    private static final Logger log = LoggerFactory.getLogger(InventoryEventConsumerConfig.class);
    private final OrderService orderService;

    @KafkaListener(topics = "inventory-events", groupId = "order-group")
    public void listen(Message<InventoryEvent> message) {
        log.info("Received record: {}", message);
        orderService.updateOrder(message.getPayload());
    }
}
