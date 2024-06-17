package com.luv2code.productservice.repository;

import com.luv2code.productservice.entity.Product;
import org.socialsignin.spring.data.dynamodb.repository.DynamoDBPagingAndSortingRepository;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.socialsignin.spring.data.dynamodb.repository.EnableScanCount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProductRepository extends DynamoDBPagingAndSortingRepository<Product, String> {

    @EnableScan
    @EnableScanCount
    Page<Product> findAll(Pageable pageable);

    Product save(Product product);

    Optional<Product> findByProductId(String productId);

    void delete(Product product);
}
