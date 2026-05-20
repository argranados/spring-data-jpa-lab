package com.ciberaccion.ecommercelab.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ciberaccion.ecommercelab.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);

    List<Customer> findByNameContainingIgnoreCase(String name);

    Page<Customer> findAll(Pageable pageable);
}
