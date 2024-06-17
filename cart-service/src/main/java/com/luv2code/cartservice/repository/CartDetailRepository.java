package com.luv2code.cartservice.repository;

import com.luv2code.cartservice.entity.CartDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartDetailRepository extends JpaRepository<CartDetail, Long> {
}
