package com.minierp.mini_erp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDTO {

    @NotBlank(message = "Ürün adı boş olamaz")
    private String name;

    @NotBlank(message = "SKU boş olamaz")
    private String sku;

    @NotNull(message = "Fiyat boş olamaz")
    private BigDecimal price;

    @NotNull(message = "Stok miktarı boş olamaz")
    private Integer quantity;

    private Integer criticalStockLevel;

    @NotNull(message = "Kategori ID boş olamaz")
    private Long categoryId;
}

