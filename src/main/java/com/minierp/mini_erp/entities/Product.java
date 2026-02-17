package com.minierp.mini_erp.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer quantity;

    // Ürün için kritik stok seviyesi (örneğin: 10 adet altı kritik kabul edilsin)
    // Şimdilik nullable bırakıyoruz; doldurulmazsa iş mantığında 0 gibi ele alabiliriz.
    @Column
    private Integer criticalStockLevel;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @JsonBackReference
    @ToString.Exclude   // Lombok toString içinde yazma
    private Category category;
}