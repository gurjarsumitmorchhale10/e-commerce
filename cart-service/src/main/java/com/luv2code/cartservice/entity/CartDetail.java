package com.luv2code.cartservice.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

@Entity
@Data
@Builder
public class CartDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long cartDetailId;

    @ManyToOne
    @Column(name = "cart_id")
    private Cart cart;

    private String product;
    private long quantity;
    private Double price;

}
