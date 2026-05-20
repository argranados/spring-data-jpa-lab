package com.ciberaccion.ecommercelab.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.data.domain.Page;

import com.ciberaccion.ecommercelab.dto.ProductDTO;
import com.ciberaccion.ecommercelab.entity.Product;
import com.ciberaccion.ecommercelab.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/search")
    public List<ProductDTO> search(@RequestParam String keyword) {
        return productService.searchByKeyword(keyword);
    }

    @GetMapping("/price-range")
    public List<ProductDTO> byPriceRange(@RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        return productService.findByPriceRange(min, max);
    }

    @GetMapping("/category/{name}")
    public List<ProductDTO> byCategory(@PathVariable String name) {
        return productService.findByCategoryName(name);
    }

    @GetMapping("/out-of-stock")
    public List<ProductDTO> outOfStock() {
        return productService.findOutOfStock();
    }

    @GetMapping("/category/{id}/paginated")
    public Page<ProductDTO> byCategoryPaginated(@PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return productService.findByCategoryPaginated(id, page, size);
    }

    // Provoca el problema N+1, sin ProductDTO sin toDTO
    @GetMapping("/all-with-category")
    public List<ProductDTO> allWithCategory() {
        return productService.findAllWithCategory();
    }
}
