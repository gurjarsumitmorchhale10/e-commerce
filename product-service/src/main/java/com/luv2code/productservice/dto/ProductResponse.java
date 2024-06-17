package com.luv2code.productservice.dto;

import com.luv2code.productservice.entity.Product;

public record ProductResponse(String productId, String name, String description, double price) {

    public static ProductResponse productResponseMapper(Product product) {
        return new ProductResponse(product.getProductId(), product.getName(), product.getDescription(), product.getPrice());
    }
}
