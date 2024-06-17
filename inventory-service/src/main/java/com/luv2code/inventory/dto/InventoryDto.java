package com.luv2code.inventory.dto;

import com.luv2code.inventory.entity.InventoryBalance;

import java.io.Serializable;

/**
 * A DTO for the {@link InventoryBalance} entity
 */
public record InventoryDto(String skuCode, Integer quantity) implements Serializable {
}