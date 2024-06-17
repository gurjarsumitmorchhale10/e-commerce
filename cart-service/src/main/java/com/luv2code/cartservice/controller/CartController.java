package com.luv2code.cartservice.controller;


import com.luv2code.cartservice.dto.CartRequest;
import com.luv2code.cartservice.entity.Cart;
import com.luv2code.cartservice.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;


    @GetMapping
    public ResponseEntity<Cart> viewCart(@RequestHeader("logged-in-user") String username) {
        return ResponseEntity.ok(cartService.viewCart(username));
    }

    @PostMapping("/remove")
    public ResponseEntity<Cart> removeItem(@RequestHeader("logged-in-user") String username, @RequestBody CartRequest cartRequest) {
        return ResponseEntity.ok(cartService.removeItem(username,cartRequest));
    }

    @PostMapping("/add")
    public ResponseEntity<Cart> addItem(@RequestHeader("logged-in-user") String username, @RequestBody CartRequest cartRequest) {
        return ResponseEntity.ok(cartService.addItem(username,cartRequest));
    }


}
