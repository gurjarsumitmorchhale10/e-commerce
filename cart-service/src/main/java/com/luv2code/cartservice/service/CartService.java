package com.luv2code.cartservice.service;

import com.luv2code.cartservice.entity.Cart;
import com.luv2code.cartservice.entity.CartDetail;
import com.luv2code.cartservice.dto.CartRequest;
import com.luv2code.cartservice.dto.ProductResponse;
import com.luv2code.cartservice.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final WebClient.Builder webClientBuilder;


    public Cart addItem(String username, CartRequest cartRequest) {

        Cart savedCart = cartRepository.findCartByUsername(username)
                .orElse(
                        Cart.builder()
                                .username(username)
                                .cartDetailList(List.of(CartDetail.builder()
                                        .product(cartRequest.product())
                                        .build()))
                                .build()
                );


        ProductResponse productDetail = webClientBuilder.baseUrl("lb:product-service/api/product").build()
                .get()
                .uri(cartRequest.product())
                .retrieve()
                .bodyToMono(ProductResponse.class)
                .block();

        if (productDetail == null) throw new IllegalArgumentException("Product requested to add in cart, does not exist!");

        savedCart.getCartDetailList()
                .forEach(cartDetail -> {
                    if(cartDetail.getProduct().equals(cartRequest.product())){
                        cartDetail.setPrice(productDetail.price());
                        cartDetail.setQuantity(cartDetail.getQuantity() + cartRequest.quantity());
                    }
                });

        return cartRepository.save(savedCart);
    }

    public Cart removeItem(String username, CartRequest cartRequest) {
        return addItem(username, new CartRequest(cartRequest.product(), cartRequest.quantity() * -1));
    }

    public Cart viewCart(String username){
        return cartRepository.findCartByUsername(username).orElse(Cart.builder().username(username).build());
    }

}
