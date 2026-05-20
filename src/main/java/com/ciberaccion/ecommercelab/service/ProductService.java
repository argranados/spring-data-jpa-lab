package com.ciberaccion.ecommercelab.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ciberaccion.ecommercelab.dto.ProductDTO;
import com.ciberaccion.ecommercelab.entity.Category;
import com.ciberaccion.ecommercelab.entity.Product;
import com.ciberaccion.ecommercelab.repository.CategoryRepository;
import com.ciberaccion.ecommercelab.repository.ProductRepository;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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

    private ProductDTO toDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setCategoryName(product.getCategory() != null
                ? product.getCategory().getName()
                : null);
        return dto;
    }
}
