package com.ciberaccion.ecommercelab.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ciberaccion.ecommercelab.entity.Category;
import com.ciberaccion.ecommercelab.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Buscar por nombre exacto
    Optional<Product> findByName(String name);

    // Buscar por categoría
    List<Product> findByCategory(Category category);

    // Buscar por nombre que contenga un texto (LIKE)
    List<Product> findByNameContainingIgnoreCase(String keyword);

    // Productos con precio menor a X
    List<Product> findByPriceLessThan(BigDecimal price);

    // Productos en stock (stock mayor a 0)
    List<Product> findByStockGreaterThan(Integer stock);

    // Productos por categoría ordenados por precio
    List<Product> findByCategoryOrderByPriceAsc(Category category);

    // Existe un producto con ese nombre?
    boolean existsByName(String name);

    // Contar productos por categoría
    long countByCategory(Category category);

    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :min AND :max")
    List<Product> findByPriceRange(@Param("min") BigDecimal min,
            @Param("max") BigDecimal max);

    @Query("SELECT p FROM Product p JOIN p.category c WHERE c.name = :categoryName")
    List<Product> findByCategoryName(@Param("categoryName") String categoryName);

    @Query("SELECT p FROM Product p WHERE p.stock = 0")
    List<Product> findOutOfStock();

    @Query("SELECT p FROM Product p WHERE p.category = :category")
    Page<Product> findByCategoryPaginated(@Param("category") Category category,
            Pageable pageable);
}
