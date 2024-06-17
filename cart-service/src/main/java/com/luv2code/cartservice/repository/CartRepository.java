package com.luv2code.cartservice.repository;

import com.luv2code.cartservice.entity.Cart;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CartRepository extends CrudRepository<Cart, Long> {

    Optional<Cart> findCartByUsername(String username);
}
