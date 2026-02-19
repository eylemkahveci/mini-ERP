package com.minierp.mini_erp.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@SQLDelete(sql = "UPDATE products SET active = false WHERE p_id = ?")
@Where(clause = "active = true")
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pId;

    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String sku;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer quantity;

    // Ürün için kritik stok seviyesi (örneğin: 10 adet altı kritik kabul edilsin)
    // Şimdilik nullable bırakıyoruz; doldurulmazsa iş mantığında 0 gibi ele alabiliriz.
    @Column
    private Integer criticalStockLevel;

    @Transient
    public String getStatus() {
        int qty = quantity != null ? quantity : 0;
        int critical = criticalStockLevel != null ? criticalStockLevel : 0;

        if (qty <= 0) {
            return "TÜKENDİ";
        }
        if (qty <= critical) {
            return "KRİTİK";
        }
        return "OK";
    }

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @JsonBackReference
    @ToString.Exclude   // Lombok toString içinde yazma
    private Category category;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean active = true;

    @Transient
    public String getCategoryName() {
        return category != null ? category.getName() : null;
    }

    @Transient
    public Long getCategoryId() {
        return category != null ? category.getCId() : null;
    }
}
