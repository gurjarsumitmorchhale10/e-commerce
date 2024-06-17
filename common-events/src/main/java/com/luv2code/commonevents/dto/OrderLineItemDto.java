package com.luv2code.commonevents.dto;



import java.io.Serializable;
import java.math.BigDecimal;

public record OrderLineItemDto(Long id, String skuCode, BigDecimal price, Integer quantity) implements Serializable {
}