package com.luv2code.inventory.service;

import com.luv2code.commonevents.dto.OrderDto;
import com.luv2code.commonevents.dto.OrderLineItemDto;
import com.luv2code.commonevents.event.InventoryEvent;
import com.luv2code.commonevents.event.InventoryStatus;
import com.luv2code.commonevents.event.OrderEvent;
import com.luv2code.inventory.dto.InventoryDto;
import com.luv2code.inventory.entity.InventoryBalance;
import com.luv2code.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final KafkaTemplate<String, InventoryEvent> kafkaTemplate;


    private void publishInventoryEvent(OrderDto orderDto, InventoryStatus inventoryStatus) {
        InventoryEvent inventoryEvent = new InventoryEvent(orderDto, inventoryStatus);
        kafkaTemplate.send("inventory-events", inventoryEvent);
    }

    public void addStock(InventoryDto inventoryDto) {

        InventoryBalance inventoryBalance = inventoryRepository.findBySkuCode(inventoryDto.skuCode()).orElse(
                InventoryBalance.builder()
                        .skuCode(inventoryDto.skuCode())
                        .quantity(0)
                        .build()
        );

        inventoryBalance.setQuantity(inventoryBalance.getQuantity() + inventoryDto.quantity());
        inventoryRepository.save(inventoryBalance);
        log.info("Added stock to inventory balance: {}", inventoryBalance);
    }

    @Transactional
    public void validateStock(OrderEvent orderEvent) {
        List<OrderLineItemDto> orderLineItems = orderEvent.getOrderDto().getOrderLineItems();

        List<String> skuCodes = orderLineItems.stream().map(OrderLineItemDto::skuCode).toList();

        InventoryStatus status = InventoryStatus.AVAILABLE;

        Map<String, InventoryBalance> skuCodeInventoryItemMap = inventoryRepository.findBySkuCodeIn(skuCodes).stream().collect(Collectors.toMap(
                InventoryBalance::getSkuCode, inventoryBalance -> inventoryBalance
        ));


        for (OrderLineItemDto orderLineItem : orderLineItems) {
            InventoryBalance balance =  skuCodeInventoryItemMap.getOrDefault(orderLineItem.skuCode(), InventoryBalance
                    .builder()
                    .skuCode(orderLineItem.skuCode())
                    .quantity(0).build());
            if (balance.getQuantity() < orderLineItem.quantity()) {
                status = InventoryStatus.UNAVAILABLE;
            }

            balance.setQuantity(balance.getQuantity() - orderLineItem.quantity());
            skuCodeInventoryItemMap.put(orderLineItem.skuCode(), balance);
        }
        log.info("Inventory Status for Order {} is: {} ", orderEvent.getOrderDto().getOrderId(), status);
        inventoryRepository.saveAll(skuCodeInventoryItemMap.values());
        publishInventoryEvent(orderEvent.getOrderDto(), status);
    }

    @Transactional
    public void updateStock(OrderEvent orderEvent) {
        List<OrderLineItemDto> orderLineItems = orderEvent.getOrderDto().getOrderLineItems();

        List<String> skuCodes = orderLineItems.stream().map(OrderLineItemDto::skuCode).toList();

        Map<String, InventoryBalance> skuCodeInventoryItemMap = inventoryRepository.findBySkuCodeIn(skuCodes).stream().collect(Collectors.toMap(
                InventoryBalance::getSkuCode, inventoryBalance -> inventoryBalance
        ));


        for (OrderLineItemDto orderLineItem : orderLineItems) {
            InventoryBalance balance =  skuCodeInventoryItemMap.getOrDefault(orderLineItem.skuCode(), InventoryBalance
                    .builder()
                    .skuCode(orderLineItem.skuCode())
                    .quantity(0).build());

            balance.setQuantity(balance.getQuantity() + orderLineItem.quantity());
            skuCodeInventoryItemMap.put(orderLineItem.skuCode(), balance);
        }

        log.info("update stock to inventory balance: {}", skuCodeInventoryItemMap);
        inventoryRepository.saveAll(skuCodeInventoryItemMap.values());
    }
}
