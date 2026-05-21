package com.ciberaccion.ecommercelab.dto;

import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class OrderRequest {
    private Long customerId;
    private Map<Long, Integer> productQuantities; // productId -> quantity
}
