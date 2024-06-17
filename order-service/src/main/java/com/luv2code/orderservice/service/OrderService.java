package com.luv2code.orderservice.service;

import com.luv2code.commonevents.dto.OrderDto;
import com.luv2code.commonevents.dto.OrderLineItemDto;
import com.luv2code.commonevents.event.InventoryEvent;
import com.luv2code.commonevents.event.InventoryStatus;
import com.luv2code.commonevents.event.OrderEvent;
import com.luv2code.commonevents.event.OrderStatus;
import com.luv2code.orderservice.entity.Order;
import com.luv2code.orderservice.entity.OrderLineItem;
import com.luv2code.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;

    private final KafkaTemplate<String, OrderEvent> orderKafkaTemplate;

    private void publishOrderEvent(OrderDto orderDto, OrderStatus orderStatus) {
        OrderEvent orderEvent = new OrderEvent(orderStatus, orderDto);
        orderKafkaTemplate.send("order-events", orderEvent);
    }



    @Transactional
    public String placeOrder(OrderDto orderDto, String loggedInUser) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setCustomerName(loggedInUser);
        order.setOrderLineItems(orderDto.orderLineItems().stream()
                        .map(orderLineItemMapper())
                        .toList());

        //save
        Order saved = orderRepository.save(order);
        orderDto.setOrderId(saved.getId());

        //publish event to topic
        publishOrderEvent(orderDto, OrderStatus.ORDER_ACCEPTED);
        log.info("Order {} placed", order.getOrderNumber());
        return "Order Accepted!";
    }



    private Function<OrderLineItemDto, OrderLineItem> orderLineItemMapper() {
        return orderLineItemDto ->
            OrderLineItem.builder()
                    .skuCode(orderLineItemDto.skuCode())
                    .price(orderLineItemDto.price())
                    .quantity(orderLineItemDto.quantity())
                    .build();

    }

    @Transactional
    public void updateOrder(InventoryEvent event) {
        OrderDto orderDto = event.getOrderDto();
        if(event.getInventoryStatus().equals(InventoryStatus.AVAILABLE)) {
            orderRepository.findById(orderDto.getOrderId()).ifPresent(order -> {
                order.setOrderStatus(OrderStatus.ORDER_FULFILLED);
                orderRepository.save(order);
                log.info("Order {} Completed!", order.getOrderNumber());
            });
        } else {
            orderRepository.findById(orderDto.getOrderId()).ifPresent(order -> {
                order.setOrderStatus(OrderStatus.ORDER_REJECTED);
                orderRepository.save(order);
                publishOrderEvent(orderDto, OrderStatus.ORDER_REJECTED);
                log.info("Order {} Rejected!", order.getOrderNumber());
            });
        }
    }

    public Page<OrderDto> getOrders(String customerName, Integer page, Integer size) {
        Page<Order> orderPage = orderRepository.findByCustomerNameOrderByOrderDateDesc(customerName, PageRequest.of(page, size));

        log.info("Orders found: {} for customer: {}",
                orderPage.getTotalElements(), customerName);

        return new PageImpl<>(orderPage.stream().map(orderDtoMapper()).toList(),
                orderPage.getPageable(),
                orderPage.getTotalPages());
    }

    private Function<Order, OrderDto> orderDtoMapper() {
        return order ->
            OrderDto.builder()
                    .orderNumber(order.getOrderNumber())
                    .orderId(order.getId())
                    .orderLineItems(order.getOrderLineItems().stream().map(
                            orderLineItem -> new OrderLineItemDto(orderLineItem.getId(),
                                    orderLineItem.getSkuCode(),
                                    orderLineItem.getPrice(),
                                    orderLineItem.getQuantity()
                            )).toList())
                    .orderStatus(String.valueOf(order.getOrderStatus()))
                    .build();
    }
}
