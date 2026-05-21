package com.ciberaccion.ecommercelab.helper;

import com.ciberaccion.ecommercelab.entity.Product;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import java.math.BigDecimal;

import com.ciberaccion.ecommercelab.entity.Category;

public class ProductSpecification {

    public static Specification<Product> hasName(String name) {
        return (root, query, cb) -> 
            name == null ? null : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Product> hasCategory(String categoryName) {
        return (root, query, cb) -> {
            if (categoryName == null) return null;
            Join<Product, Category> category = root.join("category", JoinType.LEFT);
            return cb.equal(cb.lower(category.get("name")), categoryName.toLowerCase());
        };
    }

    public static Specification<Product> priceBetween(BigDecimal min, BigDecimal max) {
        return (root, query, cb) -> {
            if (min == null && max == null) return null;
            if (min == null) return cb.lessThanOrEqualTo(root.get("price"), max);
            if (max == null) return cb.greaterThanOrEqualTo(root.get("price"), min);
            return cb.between(root.get("price"), min, max);
        };
    }

    public static Specification<Product> inStock() {
        return (root, query, cb) -> 
            cb.greaterThan(root.get("stock"), 0);
    }
}
