package com.luv2code.orderservice.repository;

import com.luv2code.orderservice.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {


    Page<Order> findByCustomerNameOrderByOrderDateDesc(String customerName, Pageable pageable);
}