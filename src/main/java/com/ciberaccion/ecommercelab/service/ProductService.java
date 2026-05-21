package com.ciberaccion.ecommercelab.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ciberaccion.ecommercelab.dto.ProductDTO;
import com.ciberaccion.ecommercelab.entity.Category;
import com.ciberaccion.ecommercelab.entity.Product;
import com.ciberaccion.ecommercelab.helper.ProductSpecification;
import com.ciberaccion.ecommercelab.repository.CategoryRepository;
import com.ciberaccion.ecommercelab.repository.ProductRepository;
import com.ciberaccion.ecommercelab.repository.ProductSummary;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<ProductDTO> searchByKeyword(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> findByPriceRange(BigDecimal min, BigDecimal max) {
        return productRepository.findByPriceRange(min, max)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> findByCategoryName(String categoryName) {
        return productRepository.findByCategoryName(categoryName)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> findOutOfStock() {
        return productRepository.findOutOfStock()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> findByCategoryPaginated(Long categoryId, int page, int size) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        Pageable pageable = PageRequest.of(page, size, Sort.by("price").ascending());
        return productRepository.findByCategoryPaginated(category, pageable)
                .map(this::toDTO);
    }

    // Provoca el problema, sin ProductDTO sin toDTO
    @Transactional(readOnly = true)
    public List<ProductDTO> findAllWithCategory() {
        return productRepository.findAll() // esto lanza el N + 1, ahora probar 2da sol con EntityGraph
                // return productRepository.findAllWithCategoryFetch() // 1a sol. esto soluciona
                // N+1 con JOIN FETCH
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private ProductDTO toDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setCategoryName(product.getCategory() != null
                ? product.getCategory().getName()
                : null);
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        dto.setVersion(product.getVersion());
        return dto;
    }

    @Transactional(readOnly = true)
    public List<ProductSummary> findInStock() {
        return productRepository.findByStockGreaterThan(0);
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> findWithFilters(String name, String categoryName,
            BigDecimal minPrice, BigDecimal maxPrice,
            Boolean onlyInStock) {
        Specification<Product> spec = Specification
                .where(ProductSpecification.hasName(name))
                .and(ProductSpecification.hasCategory(categoryName))
                .and(ProductSpecification.priceBetween(minPrice, maxPrice));

        if (Boolean.TRUE.equals(onlyInStock)) {
            spec = spec.and(ProductSpecification.inStock());
        }

        return productRepository.findAll(spec)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
