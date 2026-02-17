package com.minierp.mini_erp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class StockMovementRequest {

    @NotNull
    private Long productId;

    @NotNull
    @Positive // 0 veya negatif miktar kabul etmeyelim
    private Integer quantity;

    private String description;
}

