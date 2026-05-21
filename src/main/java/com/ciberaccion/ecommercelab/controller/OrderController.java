package com.ciberaccion.ecommercelab.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ciberaccion.ecommercelab.entity.Order;
import com.ciberaccion.ecommercelab.dto.OrderRequest;
import com.ciberaccion.ecommercelab.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest request) {
        try {
            Order order = orderService.createOrder(
                request.getCustomerId(),
                request.getProductQuantities()
            );
            return ResponseEntity.ok("Order created with id: " + order.getId());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
