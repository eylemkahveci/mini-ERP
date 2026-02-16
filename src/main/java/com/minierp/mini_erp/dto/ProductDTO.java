package com.minierp.mini_erp.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductDTO {
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private Long categoryId;
}