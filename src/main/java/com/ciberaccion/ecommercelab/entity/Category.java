package com.ciberaccion.ecommercelab.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    // 💡 Regla fácil de recordar: mappedBy = no soy el dueño, pregúntale al otro lado.
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>();
}

// mappedBy = "category" le dice a Hibernate "el dueño de esta relación es el campo category en la clase Product". 
// Esto es crítico — solo un lado es el dueño y ese lado es quien tiene @JoinColumn. 
// El lado con mappedBy es solo lectura en términos de la FK
