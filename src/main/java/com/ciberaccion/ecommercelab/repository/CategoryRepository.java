package com.ciberaccion.ecommercelab.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ciberaccion.ecommercelab.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {}
