package com.minierp.mini_erp.repositories;

import com.minierp.mini_erp.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Kritik stok seviyesi tanımlı olup, mevcut miktarı bu seviyenin altında veya eşit olan ürünleri getirir.
     */
    @Query("select p from Product p " +
           "where p.criticalStockLevel is not null " +
           "and p.quantity <= p.criticalStockLevel")
    List<Product> findLowStockProducts();
}