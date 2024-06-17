package com.luv2code.inventory.config;

import com.luv2code.commonevents.event.OrderEvent;
import com.luv2code.commonevents.event.OrderStatus;
import com.luv2code.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;

@Configuration
@RequiredArgsConstructor
public class OrderEventConsumerConfig {

    private static final Logger log = LoggerFactory.getLogger(OrderEventConsumerConfig.class);
    private final InventoryService inventoryService;

    /*@Bean
    @MessageMapping("inventory-service.orderEventConsumer-in-0")
    Consumer<Message<OrderEvent>> orderEventConsumer() {
       return message -> {
           OrderEvent orderEvent = message.getPayload();
           if (orderEvent.getOrderStatus().equals(OrderStatus.ORDER_ACCEPTED)) {
               inventoryService.validateStock(orderEvent);
           } else {
               inventoryService.updateStock(orderEvent);
           }
       };
    }

    @Bean
    public Consumer<Message<?>> errorHandler() {
        return errorMessage -> log.error("Handling error message: {}", errorMessage);
    }*/

    @KafkaListener(topics = "order-events", groupId = "order-group")
    public void listen(Message<OrderEvent> message) {
        OrderEvent orderEvent = message.getPayload();
        log.info("Received order event: {}", orderEvent);
        if (orderEvent.getOrderStatus().equals(OrderStatus.ORDER_ACCEPTED)) {
            inventoryService.validateStock(orderEvent);
        } else {
            inventoryService.updateStock(orderEvent);
        }
    }
}
