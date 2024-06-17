package com.luv2code.productservice.service;

import com.luv2code.productservice.dto.ProductRequest;
import com.luv2code.productservice.dto.ProductResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {
    ProductResponse saveProduct(ProductRequest productRequest);
    Page<ProductResponse> getProducts(Integer pageNo, Integer pageSize);
    ProductResponse getProduct(String productId);
    ProductResponse updateProduct(String productId, ProductRequest productRequest);

    void deleteProduct(String productId);
}
