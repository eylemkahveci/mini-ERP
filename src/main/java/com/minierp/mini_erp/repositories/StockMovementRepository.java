package com.minierp.mini_erp.repositories;

import com.minierp.mini_erp.entities.MovementType;
import com.minierp.mini_erp.entities.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    /** Tüm hareketler, en yeniden eskiye */
    List<StockMovement> findAllByOrderByMovementDateDesc();

    /** Belirli bir ürüne ait hareketler – parametre açık sorguda kullanılıyor */
    @Query("SELECT s FROM StockMovement s WHERE s.product.pId = :productId ORDER BY s.movementDate DESC")
    List<StockMovement> findByProductIdOrderByMovementDateDesc(@Param("productId") Long productId);

    /** Hareket tipine göre (IN veya OUT), en yeniden eskiye */
    List<StockMovement> findByMovementTypeOrderByMovementDateDesc(MovementType movementType);
}

