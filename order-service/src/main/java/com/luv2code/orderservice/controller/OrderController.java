package com.luv2code.orderservice.controller;

import com.luv2code.commonevents.dto.OrderDto;
import com.luv2code.orderservice.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethod")
    @TimeLimiter(name = "inventory")
    @Retry(name="inventory")
    CompletableFuture<String> placeOrder(@RequestBody OrderDto orderDto, @RequestHeader("logged-in-user") String loggedInUser) {
        return CompletableFuture.supplyAsync(() -> orderService.placeOrder(orderDto, loggedInUser));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    CompletableFuture<Page<OrderDto>> getOrders(@RequestHeader("logged-in-user") String loggedInUser,
                                                @RequestParam(defaultValue = "0") int pageNumber,
                                                @RequestParam(defaultValue = "10") int pageSize) {
        return CompletableFuture.supplyAsync(() -> orderService.getOrders(loggedInUser, pageNumber, pageSize));

    }

    CompletableFuture<String> fallbackMethod(OrderDto orderDto, String loggedInUser, RuntimeException runtimeException) {
        return CompletableFuture.supplyAsync( () -> "oops! something went wrong, please order after some time.");
    }
}
