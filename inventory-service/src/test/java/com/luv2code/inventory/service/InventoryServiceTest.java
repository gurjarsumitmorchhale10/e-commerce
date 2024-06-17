package com.luv2code.inventory.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.luv2code.commonevents.dto.OrderDto;
import com.luv2code.commonevents.dto.OrderLineItemDto;
import com.luv2code.commonevents.event.InventoryEvent;
import com.luv2code.commonevents.event.OrderEvent;
import com.luv2code.inventory.dto.InventoryDto;
import com.luv2code.inventory.entity.InventoryBalance;
import com.luv2code.inventory.repository.InventoryRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {InventoryService.class})
@ExtendWith(SpringExtension.class)
@DisabledInAotMode
public class InventoryServiceTest {
    @MockBean
    private InventoryRepository inventoryRepository;

    @Autowired
    private InventoryService inventoryService;

    @MockBean
    private KafkaTemplate<String, InventoryEvent> kafkaTemplate;

    /**
     * Method under test: {@link InventoryService#addStock(InventoryDto)}
     */
    @Test
    void testAddStock() {
        // Arrange
        InventoryBalance inventoryBalance = new InventoryBalance();
        inventoryBalance.setId(1L);
        inventoryBalance.setQuantity(1);
        inventoryBalance.setSkuCode("Sku Code");

        InventoryBalance inventoryBalance2 = new InventoryBalance();
        inventoryBalance2.setId(1L);
        inventoryBalance2.setQuantity(1);
        inventoryBalance2.setSkuCode("Sku Code");
        Optional<InventoryBalance> ofResult = Optional.of(inventoryBalance2);
        when(inventoryRepository.save(Mockito.<InventoryBalance>any())).thenReturn(inventoryBalance);
        when(inventoryRepository.findBySkuCode(Mockito.<String>any())).thenReturn(ofResult);

        // Act
        inventoryService.addStock(new InventoryDto("Sku Code", 1));

        // Assert
        verify(inventoryRepository).findBySkuCode(eq("Sku Code"));
        verify(inventoryRepository).save(isA(InventoryBalance.class));
    }

    /**
     * Method under test: {@link InventoryService#addStock(InventoryDto)}
     */
    @Test
    void testAddStock2() {
        // Arrange
        InventoryBalance inventoryBalance = new InventoryBalance();
        inventoryBalance.setId(1L);
        inventoryBalance.setQuantity(1);
        inventoryBalance.setSkuCode("Sku Code");
        InventoryBalance inventoryBalance2 = mock(InventoryBalance.class);
        when(inventoryBalance2.getQuantity()).thenReturn(1);
        doNothing().when(inventoryBalance2).setId(Mockito.<Long>any());
        doNothing().when(inventoryBalance2).setQuantity(Mockito.<Integer>any());
        doNothing().when(inventoryBalance2).setSkuCode(Mockito.<String>any());
        inventoryBalance2.setId(1L);
        inventoryBalance2.setQuantity(1);
        inventoryBalance2.setSkuCode("Sku Code");
        Optional<InventoryBalance> ofResult = Optional.of(inventoryBalance2);
        when(inventoryRepository.save(Mockito.<InventoryBalance>any())).thenReturn(inventoryBalance);
        when(inventoryRepository.findBySkuCode(Mockito.<String>any())).thenReturn(ofResult);

        // Act
        inventoryService.addStock(new InventoryDto("Sku Code", 1));

        // Assert
        verify(inventoryBalance2).getQuantity();
        verify(inventoryBalance2).setId(eq(1L));
        verify(inventoryBalance2, atLeast(1)).setQuantity(Mockito.<Integer>any());
        verify(inventoryBalance2).setSkuCode(eq("Sku Code"));
        verify(inventoryRepository).findBySkuCode(eq("Sku Code"));
        verify(inventoryRepository).save(isA(InventoryBalance.class));
    }

    /**
     * Method under test: {@link InventoryService#validateStock(OrderEvent)}
     */
    @Test
    void testValidateStock() {
        // Arrange
        when(inventoryRepository.saveAll(Mockito.<Iterable<InventoryBalance>>any())).thenReturn(new ArrayList<>());
        when(inventoryRepository.findBySkuCodeIn(Mockito.<List<String>>any())).thenReturn(new HashSet<>());
        when(kafkaTemplate.send(Mockito.<String>any(), Mockito.<InventoryEvent>any()))
                .thenReturn(new CompletableFuture<>());

        OrderEvent orderEvent = new OrderEvent();
        OrderDto.OrderDtoBuilder orderIdResult = OrderDto.builder().orderId(1L);
        OrderDto orderDto = orderIdResult.orderLineItems(new ArrayList<>())
                .orderNumber("42")
                .orderStatus("Order Status")
                .build();
        orderEvent.setOrderDto(orderDto);

        // Act
        inventoryService.validateStock(orderEvent);

        // Assert
        verify(inventoryRepository).findBySkuCodeIn(isA(List.class));
        verify(inventoryRepository).saveAll(isA(Iterable.class));
        verify(kafkaTemplate).send(eq("inventory-events"), isA(InventoryEvent.class));
    }

    /**
     * Method under test: {@link InventoryService#validateStock(OrderEvent)}
     */
    @Test
    void testValidateStock2() {
        // Arrange
        InventoryBalance inventoryBalance = new InventoryBalance();
        inventoryBalance.setId(1L);
        inventoryBalance.setQuantity(1);
        inventoryBalance.setSkuCode("sku code");

        HashSet<InventoryBalance> inventoryBalanceSet = new HashSet<>();
        inventoryBalanceSet.add(inventoryBalance);
        when(inventoryRepository.saveAll(Mockito.<Iterable<InventoryBalance>>any())).thenReturn(new ArrayList<>());
        when(inventoryRepository.findBySkuCodeIn(Mockito.<List<String>>any())).thenReturn(inventoryBalanceSet);
        when(kafkaTemplate.send(Mockito.<String>any(), Mockito.<InventoryEvent>any()))
                .thenReturn(new CompletableFuture<>());

        OrderEvent orderEvent = new OrderEvent();
        OrderDto.OrderDtoBuilder orderIdResult = OrderDto.builder().orderId(1L);
        OrderDto orderDto = orderIdResult.orderLineItems(new ArrayList<>())
                .orderNumber("42")
                .orderStatus("Order Status")
                .build();
        orderEvent.setOrderDto(orderDto);

        // Act
        inventoryService.validateStock(orderEvent);

        // Assert
        verify(inventoryRepository).findBySkuCodeIn(isA(List.class));
        verify(inventoryRepository).saveAll(isA(Iterable.class));
        verify(kafkaTemplate).send(eq("inventory-events"), isA(InventoryEvent.class));
    }

    /**
     * Method under test: {@link InventoryService#validateStock(OrderEvent)}
     */
    @Test
    void testValidateStock3() {
        // Arrange
        InventoryBalance inventoryBalance = new InventoryBalance();
        inventoryBalance.setId(1L);
        inventoryBalance.setQuantity(1);
        inventoryBalance.setSkuCode("sku code");

        InventoryBalance inventoryBalance2 = new InventoryBalance();
        inventoryBalance2.setId(2L);
        inventoryBalance2.setQuantity(0);
        inventoryBalance2.setSkuCode("inventory-events");

        HashSet<InventoryBalance> inventoryBalanceSet = new HashSet<>();
        inventoryBalanceSet.add(inventoryBalance2);
        inventoryBalanceSet.add(inventoryBalance);
        when(inventoryRepository.saveAll(Mockito.<Iterable<InventoryBalance>>any())).thenReturn(new ArrayList<>());
        when(inventoryRepository.findBySkuCodeIn(Mockito.<List<String>>any())).thenReturn(inventoryBalanceSet);
        when(kafkaTemplate.send(Mockito.<String>any(), Mockito.<InventoryEvent>any()))
                .thenReturn(new CompletableFuture<>());

        OrderEvent orderEvent = new OrderEvent();
        OrderDto.OrderDtoBuilder orderIdResult = OrderDto.builder().orderId(1L);
        OrderDto orderDto = orderIdResult.orderLineItems(new ArrayList<>())
                .orderNumber("42")
                .orderStatus("Order Status")
                .build();
        orderEvent.setOrderDto(orderDto);

        // Act
        inventoryService.validateStock(orderEvent);

        // Assert
        verify(inventoryRepository).findBySkuCodeIn(isA(List.class));
        verify(inventoryRepository).saveAll(isA(Iterable.class));
        verify(kafkaTemplate).send(eq("inventory-events"), isA(InventoryEvent.class));
    }

    /**
     * Method under test: {@link InventoryService#validateStock(OrderEvent)}
     */
    @Test
    void testValidateStock4() {
        // Arrange
        when(inventoryRepository.saveAll(Mockito.<Iterable<InventoryBalance>>any())).thenReturn(new ArrayList<>());
        when(inventoryRepository.findBySkuCodeIn(Mockito.<List<String>>any())).thenReturn(new HashSet<>());
        when(kafkaTemplate.send(Mockito.<String>any(), Mockito.<InventoryEvent>any()))
                .thenReturn(new CompletableFuture<>());

        ArrayList<OrderLineItemDto> orderLineItems = new ArrayList<>();
        orderLineItems.add(new OrderLineItemDto(1L, "sku code", new BigDecimal("2.3"), 1));
        OrderDto orderDto = OrderDto.builder()
                .orderId(1L)
                .orderLineItems(orderLineItems)
                .orderNumber("42")
                .orderStatus("Order Status")
                .build();

        OrderEvent orderEvent = new OrderEvent();
        orderEvent.setOrderDto(orderDto);

        // Act
        inventoryService.validateStock(orderEvent);

        // Assert
        verify(inventoryRepository).findBySkuCodeIn(isA(List.class));
        verify(inventoryRepository).saveAll(isA(Iterable.class));
        verify(kafkaTemplate).send(eq("inventory-events"), isA(InventoryEvent.class));
    }

    /**
     * Method under test: {@link InventoryService#validateStock(OrderEvent)}
     */
    @Test
    void testValidateStock5() {
        // Arrange
        InventoryBalance inventoryBalance = mock(InventoryBalance.class);
        when(inventoryBalance.getSkuCode()).thenReturn("Sku Code");
        doNothing().when(inventoryBalance).setId(Mockito.<Long>any());
        doNothing().when(inventoryBalance).setQuantity(Mockito.<Integer>any());
        doNothing().when(inventoryBalance).setSkuCode(Mockito.<String>any());
        inventoryBalance.setId(1L);
        inventoryBalance.setQuantity(1);
        inventoryBalance.setSkuCode("Sku Code");

        HashSet<InventoryBalance> inventoryBalanceSet = new HashSet<>();
        inventoryBalanceSet.add(inventoryBalance);
        when(inventoryRepository.saveAll(Mockito.<Iterable<InventoryBalance>>any())).thenReturn(new ArrayList<>());
        when(inventoryRepository.findBySkuCodeIn(Mockito.<List<String>>any())).thenReturn(inventoryBalanceSet);
        when(kafkaTemplate.send(Mockito.<String>any(), Mockito.<InventoryEvent>any()))
                .thenReturn(new CompletableFuture<>());

        OrderEvent orderEvent = new OrderEvent();
        OrderDto.OrderDtoBuilder orderIdResult = OrderDto.builder().orderId(1L);
        OrderDto orderDto = orderIdResult.orderLineItems(new ArrayList<>())
                .orderNumber("42")
                .orderStatus("Order Status")
                .build();
        orderEvent.setOrderDto(orderDto);

        // Act
        inventoryService.validateStock(orderEvent);

        // Assert
        verify(inventoryBalance).getSkuCode();
        verify(inventoryBalance).setId(eq(1L));
        verify(inventoryBalance).setQuantity(eq(1));
        verify(inventoryBalance).setSkuCode(eq("Sku Code"));
        verify(inventoryRepository).findBySkuCodeIn(isA(List.class));
        verify(inventoryRepository).saveAll(isA(Iterable.class));
        verify(kafkaTemplate).send(eq("inventory-events"), isA(InventoryEvent.class));
    }

    /**
     * Method under test: {@link InventoryService#validateStock(OrderEvent)}
     */
    @Test
    void testValidateStock6() {
        // Arrange
        InventoryBalance inventoryBalance = mock(InventoryBalance.class);
        when(inventoryBalance.getQuantity()).thenReturn(1);
        when(inventoryBalance.getSkuCode()).thenReturn("Sku Code");
        doNothing().when(inventoryBalance).setId(Mockito.<Long>any());
        doNothing().when(inventoryBalance).setQuantity(Mockito.<Integer>any());
        doNothing().when(inventoryBalance).setSkuCode(Mockito.<String>any());
        inventoryBalance.setId(1L);
        inventoryBalance.setQuantity(1);
        inventoryBalance.setSkuCode("sku code");

        HashSet<InventoryBalance> inventoryBalanceSet = new HashSet<>();
        inventoryBalanceSet.add(inventoryBalance);
        when(inventoryRepository.saveAll(Mockito.<Iterable<InventoryBalance>>any())).thenReturn(new ArrayList<>());
        when(inventoryRepository.findBySkuCodeIn(Mockito.<List<String>>any())).thenReturn(inventoryBalanceSet);
        when(kafkaTemplate.send(Mockito.<String>any(), Mockito.<InventoryEvent>any()))
                .thenReturn(new CompletableFuture<>());

        ArrayList<OrderLineItemDto> orderLineItems = new ArrayList<>();
        orderLineItems.add(new OrderLineItemDto(1L, "Sku Code", new BigDecimal("2.3"), 1));
        orderLineItems.add(new OrderLineItemDto(1L, "sku code", new BigDecimal("2.3"), 1));
        OrderDto orderDto = OrderDto.builder()
                .orderId(1L)
                .orderLineItems(orderLineItems)
                .orderNumber("42")
                .orderStatus("Order Status")
                .build();

        OrderEvent orderEvent = new OrderEvent();
        orderEvent.setOrderDto(orderDto);

        // Act
        inventoryService.validateStock(orderEvent);

        // Assert
        verify(inventoryBalance, atLeast(1)).getQuantity();
        verify(inventoryBalance).getSkuCode();
        verify(inventoryBalance).setId(eq(1L));
        verify(inventoryBalance, atLeast(1)).setQuantity(Mockito.<Integer>any());
        verify(inventoryBalance).setSkuCode(eq("sku code"));
        verify(inventoryRepository).findBySkuCodeIn(isA(List.class));
        verify(inventoryRepository).saveAll(isA(Iterable.class));
        verify(kafkaTemplate).send(eq("inventory-events"), isA(InventoryEvent.class));
    }

    /**
     * Method under test: {@link InventoryService#updateStock(OrderEvent)}
     */
    @Test
    void testUpdateStock() {
        // Arrange
        when(inventoryRepository.saveAll(Mockito.<Iterable<InventoryBalance>>any())).thenReturn(new ArrayList<>());
        when(inventoryRepository.findBySkuCodeIn(Mockito.<List<String>>any())).thenReturn(new HashSet<>());

        OrderEvent orderEvent = new OrderEvent();
        OrderDto.OrderDtoBuilder orderIdResult = OrderDto.builder().orderId(1L);
        OrderDto orderDto = orderIdResult.orderLineItems(new ArrayList<>())
                .orderNumber("42")
                .orderStatus("Order Status")
                .build();
        orderEvent.setOrderDto(orderDto);

        // Act
        inventoryService.updateStock(orderEvent);

        // Assert that nothing has changed
        verify(inventoryRepository).findBySkuCodeIn(isA(List.class));
        verify(inventoryRepository).saveAll(isA(Iterable.class));
        assertEquals(1L, orderEvent.getOrderDto().getOrderId().longValue());
    }

    /**
     * Method under test: {@link InventoryService#updateStock(OrderEvent)}
     */
    @Test
    void testUpdateStock2() {
        // Arrange
        InventoryBalance inventoryBalance = new InventoryBalance();
        inventoryBalance.setId(1L);
        inventoryBalance.setQuantity(1);
        inventoryBalance.setSkuCode("sku code");

        HashSet<InventoryBalance> inventoryBalanceSet = new HashSet<>();
        inventoryBalanceSet.add(inventoryBalance);
        when(inventoryRepository.saveAll(Mockito.<Iterable<InventoryBalance>>any())).thenReturn(new ArrayList<>());
        when(inventoryRepository.findBySkuCodeIn(Mockito.<List<String>>any())).thenReturn(inventoryBalanceSet);

        OrderEvent orderEvent = new OrderEvent();
        OrderDto.OrderDtoBuilder orderIdResult = OrderDto.builder().orderId(1L);
        OrderDto orderDto = orderIdResult.orderLineItems(new ArrayList<>())
                .orderNumber("42")
                .orderStatus("Order Status")
                .build();
        orderEvent.setOrderDto(orderDto);

        // Act
        inventoryService.updateStock(orderEvent);

        // Assert that nothing has changed
        verify(inventoryRepository).findBySkuCodeIn(isA(List.class));
        verify(inventoryRepository).saveAll(isA(Iterable.class));
        assertEquals(1L, orderEvent.getOrderDto().getOrderId().longValue());
    }

    /**
     * Method under test: {@link InventoryService#updateStock(OrderEvent)}
     */
    @Test
    void testUpdateStock3() {
        // Arrange
        InventoryBalance inventoryBalance = new InventoryBalance();
        inventoryBalance.setId(1L);
        inventoryBalance.setQuantity(1);
        inventoryBalance.setSkuCode("sku code}");

        InventoryBalance inventoryBalance2 = new InventoryBalance();
        inventoryBalance2.setId(2L);
        inventoryBalance2.setQuantity(0);
        inventoryBalance2.setSkuCode("Sku Code");

        HashSet<InventoryBalance> inventoryBalanceSet = new HashSet<>();
        inventoryBalanceSet.add(inventoryBalance2);
        inventoryBalanceSet.add(inventoryBalance);
        when(inventoryRepository.saveAll(Mockito.<Iterable<InventoryBalance>>any())).thenReturn(new ArrayList<>());
        when(inventoryRepository.findBySkuCodeIn(Mockito.<List<String>>any())).thenReturn(inventoryBalanceSet);

        OrderEvent orderEvent = new OrderEvent();
        OrderDto.OrderDtoBuilder orderIdResult = OrderDto.builder().orderId(1L);
        OrderDto orderDto = orderIdResult.orderLineItems(new ArrayList<>())
                .orderNumber("42")
                .orderStatus("Order Status")
                .build();
        orderEvent.setOrderDto(orderDto);

        // Act
        inventoryService.updateStock(orderEvent);

        // Assert that nothing has changed
        verify(inventoryRepository).findBySkuCodeIn(isA(List.class));
        verify(inventoryRepository).saveAll(isA(Iterable.class));
        assertEquals(1L, orderEvent.getOrderDto().getOrderId().longValue());
    }

    /**
     * Method under test: {@link InventoryService#updateStock(OrderEvent)}
     */
    @Test
    void testUpdateStock4() {
        // Arrange
        when(inventoryRepository.saveAll(Mockito.<Iterable<InventoryBalance>>any())).thenReturn(new ArrayList<>());
        when(inventoryRepository.findBySkuCodeIn(Mockito.<List<String>>any())).thenReturn(new HashSet<>());

        ArrayList<OrderLineItemDto> orderLineItems = new ArrayList<>();
        orderLineItems.add(new OrderLineItemDto(1L, "sku code", new BigDecimal("2.3"), 1));
        OrderDto orderDto = OrderDto.builder()
                .orderId(1L)
                .orderLineItems(orderLineItems)
                .orderNumber("42")
                .orderStatus("Order Status")
                .build();

        OrderEvent orderEvent = new OrderEvent();
        orderEvent.setOrderDto(orderDto);

        // Act
        inventoryService.updateStock(orderEvent);

        // Assert
        verify(inventoryRepository).findBySkuCodeIn(isA(List.class));
        verify(inventoryRepository).saveAll(isA(Iterable.class));
    }

    /**
     * Method under test: {@link InventoryService#updateStock(OrderEvent)}
     */
    @Test
    void testUpdateStock5() {
        // Arrange
        InventoryBalance inventoryBalance = mock(InventoryBalance.class);
        when(inventoryBalance.getSkuCode()).thenReturn("Sku Code");
        doNothing().when(inventoryBalance).setId(Mockito.<Long>any());
        doNothing().when(inventoryBalance).setQuantity(Mockito.<Integer>any());
        doNothing().when(inventoryBalance).setSkuCode(Mockito.<String>any());
        inventoryBalance.setId(1L);
        inventoryBalance.setQuantity(1);
        inventoryBalance.setSkuCode("sku code");

        HashSet<InventoryBalance> inventoryBalanceSet = new HashSet<>();
        inventoryBalanceSet.add(inventoryBalance);
        when(inventoryRepository.saveAll(Mockito.<Iterable<InventoryBalance>>any())).thenReturn(new ArrayList<>());
        when(inventoryRepository.findBySkuCodeIn(Mockito.<List<String>>any())).thenReturn(inventoryBalanceSet);

        OrderEvent orderEvent = new OrderEvent();
        OrderDto.OrderDtoBuilder orderIdResult = OrderDto.builder().orderId(1L);
        OrderDto orderDto = orderIdResult.orderLineItems(new ArrayList<>())
                .orderNumber("42")
                .orderStatus("Order Status")
                .build();
        orderEvent.setOrderDto(orderDto);

        // Act
        inventoryService.updateStock(orderEvent);

        // Assert that nothing has changed
        verify(inventoryBalance).getSkuCode();
        verify(inventoryBalance).setId(eq(1L));
        verify(inventoryBalance).setQuantity(eq(1));
        verify(inventoryBalance).setSkuCode(eq("sku code"));
        verify(inventoryRepository).findBySkuCodeIn(isA(List.class));
        verify(inventoryRepository).saveAll(isA(Iterable.class));
        assertEquals(1L, orderEvent.getOrderDto().getOrderId().longValue());
    }

    /**
     * Method under test: {@link InventoryService#updateStock(OrderEvent)}
     */
    @Test
    void testUpdateStock6() {
        // Arrange
        InventoryBalance inventoryBalance = mock(InventoryBalance.class);
        when(inventoryBalance.getQuantity()).thenReturn(1);
        when(inventoryBalance.getSkuCode()).thenReturn("Sku Code");
        doNothing().when(inventoryBalance).setId(Mockito.<Long>any());
        doNothing().when(inventoryBalance).setQuantity(Mockito.<Integer>any());
        doNothing().when(inventoryBalance).setSkuCode(Mockito.<String>any());
        inventoryBalance.setId(1L);
        inventoryBalance.setQuantity(1);
        inventoryBalance.setSkuCode("sku code");

        HashSet<InventoryBalance> inventoryBalanceSet = new HashSet<>();
        inventoryBalanceSet.add(inventoryBalance);
        when(inventoryRepository.saveAll(Mockito.<Iterable<InventoryBalance>>any())).thenReturn(new ArrayList<>());
        when(inventoryRepository.findBySkuCodeIn(Mockito.<List<String>>any())).thenReturn(inventoryBalanceSet);

        ArrayList<OrderLineItemDto> orderLineItems = new ArrayList<>();
        orderLineItems.add(new OrderLineItemDto(1L, "Sku Code", new BigDecimal("2.3"), 1));
        orderLineItems.add(new OrderLineItemDto(1L, "sku code", new BigDecimal("2.3"), 1));
        OrderDto orderDto = OrderDto.builder()
                .orderId(1L)
                .orderLineItems(orderLineItems)
                .orderNumber("42")
                .orderStatus("Order Status")
                .build();

        OrderEvent orderEvent = new OrderEvent();
        orderEvent.setOrderDto(orderDto);

        // Act
        inventoryService.updateStock(orderEvent);

        // Assert
        verify(inventoryBalance).getQuantity();
        verify(inventoryBalance).getSkuCode();
        verify(inventoryBalance).setId(eq(1L));
        verify(inventoryBalance, atLeast(1)).setQuantity(Mockito.<Integer>any());
        verify(inventoryBalance).setSkuCode(eq("sku code"));
        verify(inventoryRepository).findBySkuCodeIn(isA(List.class));
        verify(inventoryRepository).saveAll(isA(Iterable.class));
    }
}
