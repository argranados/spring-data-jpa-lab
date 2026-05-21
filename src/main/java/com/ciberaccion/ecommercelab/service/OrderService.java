package com.ciberaccion.ecommercelab.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ciberaccion.ecommercelab.entity.Customer;
import com.ciberaccion.ecommercelab.entity.Order;
import com.ciberaccion.ecommercelab.entity.OrderItem;
import com.ciberaccion.ecommercelab.entity.OrderStatus;
import com.ciberaccion.ecommercelab.entity.Product;
import com.ciberaccion.ecommercelab.repository.*;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final AuditService auditService;

    @Transactional
    public Order createOrder(Long customerId, Map<Long, Integer> productQuantities) {

        auditService.log("CREATE_ORDER", "Attempt by customer: " + customerId);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setItems(new ArrayList<>());
        orderRepository.save(order);

        for (Map.Entry<Long, Integer> entry : productQuantities.entrySet()) {
            Long productId = entry.getKey();
            Integer quantity = entry.getValue();

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

            if (product.getStock() < quantity) {
                // Lanza excepción — triggerea rollback de la orden
                // pero el audit log YA SE GUARDÓ en su propia transacción
                throw new RuntimeException("Insufficient stock for: " + product.getName());
            }

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(quantity);
            item.setUnitPrice(product.getPrice());
            order.getItems().add(item);

            product.setStock(product.getStock() - quantity);
            productRepository.save(product);
        }

        return orderRepository.save(order);
    }
}
