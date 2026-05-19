package com.ciberaccion.ecommercelab.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ciberaccion.ecommercelab.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {}
