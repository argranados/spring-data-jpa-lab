package com.ciberaccion.ecommercelab.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ciberaccion.ecommercelab.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long>{

    Order save(Order order);

}
