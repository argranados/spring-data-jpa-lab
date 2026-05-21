package com.ciberaccion.ecommercelab.repository;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public interface ProductSummary {
    Long getId();
    String getName();
    BigDecimal getPrice();
}
