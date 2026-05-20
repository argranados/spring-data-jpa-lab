package com.ciberaccion.ecommercelab.config;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.ciberaccion.ecommercelab.entity.*;
import com.ciberaccion.ecommercelab.repository.*;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationListener<ApplicationReadyEvent> {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final TagRepository tagRepository;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {

        if (categoryRepository.count() > 0) return; // evita duplicados

        // Categorías
        Category electronics = new Category();
        electronics.setName("Electronics");
        electronics.setDescription("Electronic devices");
        categoryRepository.save(electronics);

        Category clothing = new Category();
        clothing.setName("Clothing");
        clothing.setDescription("Apparel and accessories");
        categoryRepository.save(clothing);

        // Tags
        Tag sale = new Tag();
        sale.setName("sale");
        tagRepository.save(sale);

        Tag newArrival = new Tag();
        newArrival.setName("new-arrival");
        tagRepository.save(newArrival);

        // Productos
        Product laptop = new Product();
        laptop.setName("Laptop Pro 15");
        laptop.setPrice(new BigDecimal("1299.99"));
        laptop.setStock(10);
        laptop.setCategory(electronics);
        laptop.setTags(List.of(sale));
        productRepository.save(laptop);

        Product phone = new Product();
        phone.setName("Smartphone X12");
        phone.setPrice(new BigDecimal("899.99"));
        phone.setStock(25);
        phone.setCategory(electronics);
        phone.setTags(List.of(newArrival));
        productRepository.save(phone);

        Product shirt = new Product();
        shirt.setName("Classic White Shirt");
        shirt.setPrice(new BigDecimal("49.99"));
        shirt.setStock(0);
        shirt.setCategory(clothing);
        shirt.setTags(List.of(sale, newArrival));
        productRepository.save(shirt);

        // Customers
        Customer alice = new Customer();
        alice.setName("Alice Smith");
        alice.setEmail("alice@example.com");
        alice.setPhone("555-0101");
        customerRepository.save(alice);

        Customer bob = new Customer();
        bob.setName("Bob Johnson");
        bob.setEmail("bob@example.com");
        bob.setPhone("555-0102");
        customerRepository.save(bob);

        System.out.println(">>> Data seeded successfully");
    }
}
