package com.luv2code.orderservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.luv2code.commonevents.dto.OrderDto;
import com.luv2code.commonevents.dto.OrderLineItemDto;
import com.luv2code.commonevents.event.InventoryEvent;
import com.luv2code.commonevents.event.InventoryStatus;
import com.luv2code.commonevents.event.OrderEvent;
import com.luv2code.commonevents.event.OrderStatus;
import com.luv2code.orderservice.entity.Order;
import com.luv2code.orderservice.repository.OrderRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {OrderService.class})
@ExtendWith(SpringExtension.class)
@DisabledInAotMode
public class OrderServiceTest {
    @MockBean
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @MockBean
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    /**
     * Method under test: {@link OrderService#placeOrder(OrderDto, String)}
     */
    @Test
    void testPlaceOrder() {
        // Arrange
        Order order = new Order();
        order.setCustomerName("Customer Name");
        order.setId(1L);
        order.setOrderDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        order.setOrderLineItems(new ArrayList<>());
        order.setOrderNumber("42");
        order.setOrderStatus(OrderStatus.ORDER_ACCEPTED);
        when(orderRepository.save(Mockito.<Order>any())).thenReturn(order);
        when(kafkaTemplate.send(Mockito.<String>any(), Mockito.<OrderEvent>any())).thenReturn(new CompletableFuture<>());
        OrderDto.OrderDtoBuilder orderIdResult = OrderDto.builder().orderId(1L);
        OrderDto orderDto = orderIdResult.orderLineItems(new ArrayList<>())
                .orderNumber("42")
                .orderStatus("Order Status")
                .build();

        // Act
        String actualPlaceOrderResult = orderService.placeOrder(orderDto, "Logged In User");

        // Assert
        verify(orderRepository).save(isA(Order.class));
        verify(kafkaTemplate).send(eq("order-events"), isA(OrderEvent.class));
        assertEquals("Order Accepted!", actualPlaceOrderResult);
        assertEquals(1L, orderDto.getOrderId().longValue());
    }

    /**
     * Method under test: {@link OrderService#placeOrder(OrderDto, String)}
     */
    @Test
    void testPlaceOrder2() {
        // Arrange
        Order order = new Order();
        order.setCustomerName("Customer Name");
        order.setId(1L);
        order.setOrderDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        order.setOrderLineItems(new ArrayList<>());
        order.setOrderNumber("42");
        order.setOrderStatus(OrderStatus.ORDER_ACCEPTED);
        when(orderRepository.save(Mockito.<Order>any())).thenReturn(order);
        when(kafkaTemplate.send(Mockito.<String>any(), Mockito.<OrderEvent>any())).thenReturn(new CompletableFuture<>());

        ArrayList<OrderLineItemDto> orderLineItems = new ArrayList<>();
        orderLineItems.add(new OrderLineItemDto(1L, "order-events", new BigDecimal("2.3"), 1));
        OrderDto orderDto = OrderDto.builder()
                .orderId(1L)
                .orderLineItems(orderLineItems)
                .orderNumber("42")
                .orderStatus("Order Status")
                .build();

        // Act
        String actualPlaceOrderResult = orderService.placeOrder(orderDto, "Logged In User");

        // Assert
        verify(orderRepository).save(isA(Order.class));
        verify(kafkaTemplate).send(eq("order-events"), isA(OrderEvent.class));
        assertEquals("Order Accepted!", actualPlaceOrderResult);
        assertEquals(1L, orderDto.getOrderId().longValue());
    }

    /**
     * Method under test: {@link OrderService#placeOrder(OrderDto, String)}
     */
    @Test
    void testPlaceOrder3() {
        // Arrange
        Order order = new Order();
        order.setCustomerName("Customer Name");
        order.setId(1L);
        order.setOrderDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        order.setOrderLineItems(new ArrayList<>());
        order.setOrderNumber("42");
        order.setOrderStatus(OrderStatus.ORDER_ACCEPTED);
        when(orderRepository.save(Mockito.<Order>any())).thenReturn(order);
        when(kafkaTemplate.send(Mockito.<String>any(), Mockito.<OrderEvent>any())).thenReturn(new CompletableFuture<>());

        ArrayList<OrderLineItemDto> orderLineItems = new ArrayList<>();
        orderLineItems.add(new OrderLineItemDto(1L, "order-events", new BigDecimal("2.3"), 1));
        orderLineItems.add(new OrderLineItemDto(1L, "order-events", new BigDecimal("2.3"), 1));
        OrderDto orderDto = OrderDto.builder()
                .orderId(1L)
                .orderLineItems(orderLineItems)
                .orderNumber("42")
                .orderStatus("Order Status")
                .build();

        // Act
        String actualPlaceOrderResult = orderService.placeOrder(orderDto, "Logged In User");

        // Assert
        verify(orderRepository).save(isA(Order.class));
        verify(kafkaTemplate).send(eq("order-events"), isA(OrderEvent.class));
        assertEquals("Order Accepted!", actualPlaceOrderResult);
        assertEquals(1L, orderDto.getOrderId().longValue());
    }

    /**
     * Method under test: {@link OrderService#updateOrder(InventoryEvent)}
     */
    @Test
    void testUpdateOrder() {
        // Arrange
        Order order = new Order();
        order.setCustomerName("Customer Name");
        order.setId(1L);
        order.setOrderDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        order.setOrderLineItems(new ArrayList<>());
        order.setOrderNumber("42");
        order.setOrderStatus(OrderStatus.ORDER_ACCEPTED);
        Optional<Order> ofResult = Optional.of(order);

        Order order2 = new Order();
        order2.setCustomerName("Customer Name");
        order2.setId(1L);
        order2.setOrderDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        order2.setOrderLineItems(new ArrayList<>());
        order2.setOrderNumber("42");
        order2.setOrderStatus(OrderStatus.ORDER_ACCEPTED);
        when(orderRepository.save(Mockito.<Order>any())).thenReturn(order2);
        when(orderRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);
        OrderDto.OrderDtoBuilder orderIdResult = OrderDto.builder().orderId(1L);
        OrderDto orderDto = orderIdResult.orderLineItems(new ArrayList<>())
                .orderNumber("42")
                .orderStatus("Order Status")
                .build();

        // Act
        orderService.updateOrder(new InventoryEvent(orderDto, InventoryStatus.AVAILABLE));

        // Assert
        verify(orderRepository).findById(eq(1L));
        verify(orderRepository).save(isA(Order.class));
    }

    /**
     * Method under test: {@link OrderService#updateOrder(InventoryEvent)}
     */
    @Test
    void testUpdateOrder2() {
        // Arrange
        Optional<Order> emptyResult = Optional.empty();
        when(orderRepository.findById(Mockito.<Long>any())).thenReturn(emptyResult);
        OrderDto.OrderDtoBuilder orderIdResult = OrderDto.builder().orderId(1L);
        OrderDto orderDto = orderIdResult.orderLineItems(new ArrayList<>())
                .orderNumber("42")
                .orderStatus("Order Status")
                .build();
        InventoryEvent event = new InventoryEvent(orderDto, InventoryStatus.AVAILABLE);

        // Act
        orderService.updateOrder(event);

        // Assert that nothing has changed
        verify(orderRepository).findById(eq(1L));
        assertEquals(1L, event.getOrderDto().getOrderId().longValue());
        assertEquals(InventoryStatus.AVAILABLE, event.getInventoryStatus());
    }

    /**
     * Method under test: {@link OrderService#updateOrder(InventoryEvent)}
     */
    @Test
    void testUpdateOrder3() {
        // Arrange
        Order order = new Order();
        order.setCustomerName("Customer Name");
        order.setId(1L);
        order.setOrderDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        order.setOrderLineItems(new ArrayList<>());
        order.setOrderNumber("42");
        order.setOrderStatus(OrderStatus.ORDER_ACCEPTED);
        Optional<Order> ofResult = Optional.of(order);

        Order order2 = new Order();
        order2.setCustomerName("Customer Name");
        order2.setId(1L);
        order2.setOrderDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        order2.setOrderLineItems(new ArrayList<>());
        order2.setOrderNumber("42");
        order2.setOrderStatus(OrderStatus.ORDER_ACCEPTED);
        when(orderRepository.save(Mockito.<Order>any())).thenReturn(order2);
        when(orderRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);
        when(kafkaTemplate.send(Mockito.<String>any(), Mockito.<OrderEvent>any())).thenReturn(new CompletableFuture<>());
        OrderDto.OrderDtoBuilder orderIdResult = OrderDto.builder().orderId(1L);
        OrderDto orderDto = orderIdResult.orderLineItems(new ArrayList<>())
                .orderNumber("42")
                .orderStatus("Order Status")
                .build();

        // Act
        orderService.updateOrder(new InventoryEvent(orderDto, InventoryStatus.UNAVAILABLE));

        // Assert
        verify(orderRepository).findById(eq(1L));
        verify(orderRepository).save(isA(Order.class));
        verify(kafkaTemplate).send(eq("order-events"), isA(OrderEvent.class));
    }

    /**
     * Method under test: {@link OrderService#updateOrder(InventoryEvent)}
     */
    @Test
    void testUpdateOrder4() {
        // Arrange
        Optional<Order> emptyResult = Optional.empty();
        when(orderRepository.findById(Mockito.<Long>any())).thenReturn(emptyResult);
        OrderDto.OrderDtoBuilder orderIdResult = OrderDto.builder().orderId(1L);
        OrderDto orderDto = orderIdResult.orderLineItems(new ArrayList<>())
                .orderNumber("42")
                .orderStatus("Order Status")
                .build();
        InventoryEvent event = new InventoryEvent(orderDto, InventoryStatus.UNAVAILABLE);

        // Act
        orderService.updateOrder(event);

        // Assert that nothing has changed
        verify(orderRepository).findById(eq(1L));
        assertEquals(1L, event.getOrderDto().getOrderId().longValue());
        assertEquals(InventoryStatus.UNAVAILABLE, event.getInventoryStatus());
    }

    /**
     * Method under test: {@link OrderService#getOrders(String, Integer, Integer)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testGetOrders() {

        // Arrange
        Order order = new Order();
        order.setCustomerName("Customer Name");
        order.setId(1L);
        order.setOrderDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        order.setOrderLineItems(new ArrayList<>());
        order.setOrderNumber("42");
        order.setOrderStatus(OrderStatus.ORDER_ACCEPTED);

        Order order2 = new Order();
        order2.setCustomerName("Customer Name");
        order2.setId(1L);
        order2.setOrderDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        order2.setOrderLineItems(new ArrayList<>());
        order2.setOrderNumber("42");
        order2.setOrderStatus(OrderStatus.ORDER_ACCEPTED);

        when(orderRepository.findByCustomerNameOrderByOrderDateDesc(Mockito.<String>any(), Mockito.<Pageable>any()))
                .thenReturn(new PageImpl<>(List.of(order, order2)));

        // Act
        Page<OrderDto> actual = orderService.getOrders("Customer Name", 1, 3);

        //
        assertEquals(actual.stream().count(), 2);
        assertEquals(actual.getTotalPages(), 1);
        assertEquals(actual.getContent().get(0).getOrderNumber(), "42");
    }

}
