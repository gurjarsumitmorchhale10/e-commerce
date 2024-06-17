package com.luv2code.productservice.service;

import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.luv2code.productservice.dto.ProductRequest;
import com.luv2code.productservice.dto.ProductResponse;
import com.luv2code.productservice.entity.Product;
import com.luv2code.productservice.exception.ResourceNotFoundException;
import com.luv2code.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService{

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;
    public ProductResponse saveProduct(ProductRequest productRequest) {
        Product product = Product.builder()
                .name(productRequest.name())
                .description(productRequest.description())
                .price(productRequest.price())
                .build();

        Product savedProduct = productRepository.save(product);

        logger.info("product {} is created!", savedProduct.getProductId());

        return ProductResponse.productResponseMapper(savedProduct);
    }

    public Page<ProductResponse> getProducts(Integer pageNo, Integer pageSize) {
        Page<Product> productPage = productRepository.findAll(PageRequest.of(pageNo, pageSize));

        return new PageImpl<>(productPage.stream().map(ProductResponse::productResponseMapper).toList(),
                productPage.getPageable(),
                productPage.getTotalPages());
    }

    public ProductResponse getProduct(String productId) {
        return ProductResponse.productResponseMapper(
                productRepository.findByProductId(productId)
                        .orElseThrow(() -> new ResourceNotFoundException("Product with Id " + productId + " not found"))
        );
    }


    @Transactional
    public ProductResponse updateProduct(String productId, ProductRequest productRequest) {
        Product product = productRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product with Id " + productId + " not found"));
        product.setName(productRequest.name());
        product.setDescription(productRequest.description());
        product.setPrice(productRequest.price());

        try {
            Product updatedProduct = productRepository.save(product);
            return ProductResponse.productResponseMapper(updatedProduct);
        } catch (ConditionalCheckFailedException e) {
            logger.error("Concurrent update request, retry Again...");
            throw new IllegalStateException("Concurrent update request, retry Again...");
        }
    }

    @Override
    public void deleteProduct(String productId) {
        Product product = productRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product with Id " + productId + " not found"));
        productRepository.delete(product);
    }
}
