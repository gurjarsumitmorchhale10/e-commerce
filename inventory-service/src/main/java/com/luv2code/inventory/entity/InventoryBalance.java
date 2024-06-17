package com.luv2code.inventory.entity;

import lombok.*;

import jakarta.persistence.*;

@Builder
@Entity
@Table(name = "inventory_item")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InventoryBalance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private  String skuCode;
    private Integer quantity;
}