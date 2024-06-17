package com.luv2code.productservice.controller;

import com.luv2code.productservice.dto.ProductRequest;
import com.luv2code.productservice.dto.ProductResponse;
import com.luv2code.productservice.service.ProductService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.Duration;

@RestController
@RequestMapping("/api/product")
public class ProductController {
    private final Bucket bucket;

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;

        Bandwidth limit = Bandwidth.classic(20, Refill.greedy(10, Duration.ofMinutes(1)));
        this.bucket = Bucket.builder()
                .addLimit(limit)
                .build();
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest productRequest) {

        ProductResponse productResponse = productService.saveProduct(productRequest);
        return ResponseEntity.created(ServletUriComponentsBuilder.
                fromCurrentRequest()
                .path("/"+productResponse.productId())
                .build()
                .toUri()).body(productResponse);
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getProducts(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                                             @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        if (bucket.tryConsume(1)) {
            Page<ProductResponse> products = productService.getProducts(pageNo, pageSize);
            return ResponseEntity.ok(products);
        }

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable String productId) {
        ProductResponse product = productService.getProduct(productId);
        return ResponseEntity.ok(product);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable String productId, @RequestBody ProductRequest productRequest) {
        ProductResponse product = productService.updateProduct(productId, productRequest);
        return ResponseEntity.ok(product);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteProduct(@RequestParam String productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }
}
