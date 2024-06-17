package com.luv2code.productservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.luv2code.productservice.dto.ProductRequest;
import com.luv2code.productservice.dto.ProductResponse;
import com.luv2code.productservice.entity.Product;
import com.luv2code.productservice.exception.ResourceNotFoundException;
import com.luv2code.productservice.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.socialsignin.spring.data.dynamodb.domain.UnpagedPageImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {ProductServiceImpl.class})
@ExtendWith(SpringExtension.class)
@DisabledInAotMode
public class ProductServiceImplTest {
    @MockBean
    private ProductRepository productRepository;

    @Autowired
    private ProductServiceImpl productServiceImpl;

    /**
     * Method under test: {@link ProductServiceImpl#saveProduct(ProductRequest)}
     */
    @Test
    void testSaveProduct() {
        // Arrange
        Product buildResult = Product.builder()
                .description("The characteristics of someone or something")
                .name("Name")
                .price(10.0d)
                .productId("42")
                .version(1L)
                .build();
        when(productRepository.save(Mockito.<Product>any())).thenReturn(buildResult);

        // Act
        ProductResponse actualSaveProductResult = productServiceImpl
                .saveProduct(new ProductRequest("Name", "The characteristics of someone or something", 10.0d));

        // Assert
        verify(productRepository).save(isA(Product.class));
        assertEquals("42", actualSaveProductResult.productId());
        assertEquals("Name", actualSaveProductResult.name());
        assertEquals("The characteristics of someone or something", actualSaveProductResult.description());
        assertEquals(10.0d, actualSaveProductResult.price());
    }

    /**
     * Method under test: {@link ProductServiceImpl#saveProduct(ProductRequest)}
     */
    @Test
    void testSaveProduct2() {
        // Arrange
        when(productRepository.save(Mockito.<Product>any()))
                .thenThrow(new ConditionalCheckFailedException("An error occurred"));

        // Act and Assert
        assertThrows(ConditionalCheckFailedException.class, () -> productServiceImpl
                .saveProduct(new ProductRequest("Name", "The characteristics of someone or something", 10.0d)));
        verify(productRepository).save(isA(Product.class));
    }

    /**
     * Method under test: {@link ProductServiceImpl#getProducts(Integer, Integer)}
     */
    @Test
    void testGetProducts() {
        // Arrange
        when(productRepository.findAll(Mockito.<Pageable>any())).thenReturn(new UnpagedPageImpl<>(new ArrayList<>(), 1L));

        // Act
        Page<ProductResponse> actualProducts = productServiceImpl.getProducts(1, 3);

        // Assert
        verify(productRepository).findAll(isA(Pageable.class));
        assertTrue(actualProducts.toList().isEmpty());
    }

    /**
     * Method under test: {@link ProductServiceImpl#getProducts(Integer, Integer)}
     */
    @Test
    void testGetProducts2() {
        // Arrange
        ArrayList<Product> content = new ArrayList<>();
        Product buildResult = Product.builder()
                .description("The characteristics of someone or something")
                .name("Name")
                .price(10.0d)
                .productId("42")
                .version(1L)
                .build();
        content.add(buildResult);
        UnpagedPageImpl<Product> unpagedPageImpl = new UnpagedPageImpl<>(content, 1L);

        when(productRepository.findAll(Mockito.<Pageable>any())).thenReturn(unpagedPageImpl);

        // Act
        Page<ProductResponse> actualProducts = productServiceImpl.getProducts(1, 3);

        // Assert
        verify(productRepository).findAll(isA(Pageable.class));
        List<ProductResponse> toListResult = actualProducts.toList();
        assertEquals(1, toListResult.size());
        ProductResponse getResult = toListResult.get(0);
        assertEquals("42", getResult.productId());
        assertEquals("Name", getResult.name());
        assertEquals("The characteristics of someone or something", getResult.description());
        assertEquals(10.0d, getResult.price());
    }

    /**
     * Method under test: {@link ProductServiceImpl#getProducts(Integer, Integer)}
     */
    @Test
    void testGetProducts3() {
        // Arrange
        ArrayList<Product> content = new ArrayList<>();
        Product buildResult = Product.builder()
                .description("The characteristics of someone or something")
                .name("Name")
                .price(10.0d)
                .productId("42")
                .version(1L)
                .build();
        content.add(buildResult);
        Product buildResult2 = Product.builder()
                .description("The characteristics of someone or something")
                .name("Name")
                .price(10.0d)
                .productId("42")
                .version(1L)
                .build();
        content.add(buildResult2);
        UnpagedPageImpl<Product> unpagedPageImpl = new UnpagedPageImpl<>(content, 1L);

        when(productRepository.findAll(Mockito.<Pageable>any())).thenReturn(unpagedPageImpl);

        // Act
        Page<ProductResponse> actualProducts = productServiceImpl.getProducts(1, 3);

        // Assert
        verify(productRepository).findAll(isA(Pageable.class));
        List<ProductResponse> toListResult = actualProducts.toList();
        assertEquals(2, toListResult.size());
        ProductResponse getResult = toListResult.get(0);
        assertEquals("42", getResult.productId());
        ProductResponse getResult2 = toListResult.get(1);
        assertEquals("42", getResult2.productId());
        assertEquals("Name", getResult.name());
        assertEquals("Name", getResult2.name());
        assertEquals("The characteristics of someone or something", getResult.description());
        assertEquals("The characteristics of someone or something", getResult2.description());
        assertEquals(10.0d, getResult.price());
        assertEquals(10.0d, getResult2.price());
    }

    /**
     * Method under test: {@link ProductServiceImpl#getProduct(String)}
     */
    @Test
    void testGetProduct() {
        // Arrange
        Product buildResult = Product.builder()
                .description("The characteristics of someone or something")
                .name("Name")
                .price(10.0d)
                .productId("42")
                .version(1L)
                .build();
        Optional<Product> ofResult = Optional.of(buildResult);
        when(productRepository.findByProductId(Mockito.<String>any())).thenReturn(ofResult);

        // Act
        ProductResponse actualProduct = productServiceImpl.getProduct("42");

        // Assert
        verify(productRepository).findByProductId(eq("42"));
        assertEquals("42", actualProduct.productId());
        assertEquals("Name", actualProduct.name());
        assertEquals("The characteristics of someone or something", actualProduct.description());
        assertEquals(10.0d, actualProduct.price());
    }

    /**
     * Method under test: {@link ProductServiceImpl#getProduct(String)}
     */
    @Test
    void testGetProduct2() {
        // Arrange
        Optional<Product> emptyResult = Optional.empty();
        when(productRepository.findByProductId(Mockito.<String>any())).thenReturn(emptyResult);

        // Act and Assert
        assertThrows(ResourceNotFoundException.class, () -> productServiceImpl.getProduct("42"));
        verify(productRepository).findByProductId(eq("42"));
    }

    /**
     * Method under test: {@link ProductServiceImpl#getProduct(String)}
     */
    @Test
    void testGetProduct3() {
        // Arrange
        when(productRepository.findByProductId(Mockito.<String>any()))
                .thenThrow(new ConditionalCheckFailedException("An error occurred"));

        // Act and Assert
        assertThrows(ConditionalCheckFailedException.class, () -> productServiceImpl.getProduct("42"));
        verify(productRepository).findByProductId(eq("42"));
    }

    /**
     * Method under test:
     * {@link ProductServiceImpl#updateProduct(String, ProductRequest)}
     */
    @Test
    void testUpdateProduct() {
        // Arrange
        Product buildResult = Product.builder()
                .description("The characteristics of someone or something")
                .name("Name")
                .price(10.0d)
                .productId("42")
                .version(1L)
                .build();
        when(productRepository.save(Mockito.<Product>any())).thenReturn(buildResult);
        Product buildResult2 = Product.builder()
                .description("The characteristics of someone or something")
                .name("Name")
                .price(10.0d)
                .productId("42")
                .version(1L)
                .build();
        Optional<Product> ofResult = Optional.of(buildResult2);
        when(productRepository.findByProductId(Mockito.<String>any())).thenReturn(ofResult);

        // Act
        ProductResponse actualUpdateProductResult = productServiceImpl.updateProduct("42",
                new ProductRequest("Name", "The characteristics of someone or something", 10.0d));

        // Assert
        verify(productRepository).findByProductId(eq("42"));
        verify(productRepository).save(isA(Product.class));
        assertEquals("42", actualUpdateProductResult.productId());
        assertEquals("Name", actualUpdateProductResult.name());
        assertEquals("The characteristics of someone or something", actualUpdateProductResult.description());
        assertEquals(10.0d, actualUpdateProductResult.price());
    }

    /**
     * Method under test:
     * {@link ProductServiceImpl#updateProduct(String, ProductRequest)}
     */
    @Test
    void testUpdateProduct2() {
        // Arrange
        when(productRepository.save(Mockito.<Product>any()))
                .thenThrow(new ConditionalCheckFailedException("An error occurred"));
        Product buildResult = Product.builder()
                .description("The characteristics of someone or something")
                .name("Name")
                .price(10.0d)
                .productId("42")
                .version(1L)
                .build();
        Optional<Product> ofResult = Optional.of(buildResult);
        when(productRepository.findByProductId(Mockito.<String>any())).thenReturn(ofResult);

        // Act and Assert
        assertThrows(IllegalStateException.class, () -> productServiceImpl.updateProduct("42",
                new ProductRequest("Name", "The characteristics of someone or something", 10.0d)));
        verify(productRepository).findByProductId(eq("42"));
        verify(productRepository).save(isA(Product.class));
    }

    /**
     * Method under test:
     * {@link ProductServiceImpl#updateProduct(String, ProductRequest)}
     */
    @Test
    void testUpdateProduct3() {
        // Arrange
        when(productRepository.save(Mockito.<Product>any())).thenThrow(new IllegalStateException("foo"));
        Product buildResult = Product.builder()
                .description("The characteristics of someone or something")
                .name("Name")
                .price(10.0d)
                .productId("42")
                .version(1L)
                .build();
        Optional<Product> ofResult = Optional.of(buildResult);
        when(productRepository.findByProductId(Mockito.<String>any())).thenReturn(ofResult);

        // Act and Assert
        assertThrows(IllegalStateException.class, () -> productServiceImpl.updateProduct("42",
                new ProductRequest("Name", "The characteristics of someone or something", 10.0d)));
        verify(productRepository).findByProductId(eq("42"));
        verify(productRepository).save(isA(Product.class));
    }

    /**
     * Method under test:
     * {@link ProductServiceImpl#updateProduct(String, ProductRequest)}
     */
    @Test
    void testUpdateProduct4() {
        // Arrange
        Optional<Product> emptyResult = Optional.empty();
        when(productRepository.findByProductId(Mockito.<String>any())).thenReturn(emptyResult);

        // Act and Assert
        assertThrows(ResourceNotFoundException.class, () -> productServiceImpl.updateProduct("42",
                new ProductRequest("Name", "The characteristics of someone or something", 10.0d)));
        verify(productRepository).findByProductId(eq("42"));
    }

    /**
     * Method under test: {@link ProductServiceImpl#deleteProduct(String)}
     */
    @Test
    void testDeleteProduct() {
        // Arrange
        doNothing().when(productRepository).delete(Mockito.<Product>any());
        Product buildResult = Product.builder()
                .description("The characteristics of someone or something")
                .name("Name")
                .price(10.0d)
                .productId("42")
                .version(1L)
                .build();
        Optional<Product> ofResult = Optional.of(buildResult);
        when(productRepository.findByProductId(Mockito.<String>any())).thenReturn(ofResult);

        // Act
        productServiceImpl.deleteProduct("42");

        // Assert that nothing has changed
        verify(productRepository).delete(isA(Product.class));
        verify(productRepository).findByProductId(eq("42"));
    }

    /**
     * Method under test: {@link ProductServiceImpl#deleteProduct(String)}
     */
    @Test
    void testDeleteProduct2() {
        // Arrange
        doThrow(new ConditionalCheckFailedException("An error occurred")).when(productRepository)
                .delete(Mockito.<Product>any());
        Product buildResult = Product.builder()
                .description("The characteristics of someone or something")
                .name("Name")
                .price(10.0d)
                .productId("42")
                .version(1L)
                .build();
        Optional<Product> ofResult = Optional.of(buildResult);
        when(productRepository.findByProductId(Mockito.<String>any())).thenReturn(ofResult);

        // Act and Assert
        assertThrows(ConditionalCheckFailedException.class, () -> productServiceImpl.deleteProduct("42"));
        verify(productRepository).delete(isA(Product.class));
        verify(productRepository).findByProductId(eq("42"));
    }

    /**
     * Method under test: {@link ProductServiceImpl#deleteProduct(String)}
     */
    @Test
    void testDeleteProduct3() {
        // Arrange
        Optional<Product> emptyResult = Optional.empty();
        when(productRepository.findByProductId(Mockito.<String>any())).thenReturn(emptyResult);

        // Act and Assert
        assertThrows(ResourceNotFoundException.class, () -> productServiceImpl.deleteProduct("42"));
        verify(productRepository).findByProductId(eq("42"));
    }
}
