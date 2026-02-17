package com.minierp.mini_erp.controllers;

import com.minierp.mini_erp.dto.StockMovementRequest;
import com.minierp.mini_erp.entities.MovementType;
import com.minierp.mini_erp.entities.StockMovement;
import com.minierp.mini_erp.services.StockMovementService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
public class StockMovementController {

    private final StockMovementService stockMovementService;

    public StockMovementController(StockMovementService stockMovementService) {
        this.stockMovementService = stockMovementService;
    }

    // STOK GİRİŞİ (IN)
    @PostMapping("/in")
    public ResponseEntity<StockMovement> createStockIn(@Valid @RequestBody StockMovementRequest request) {
        return ResponseEntity.ok(stockMovementService.createInMovement(request));
    }

    // STOK ÇIKIŞI (OUT)
    @PostMapping("/out")
    public ResponseEntity<StockMovement> createStockOut(@Valid @RequestBody StockMovementRequest request) {
        return ResponseEntity.ok(stockMovementService.createOutMovement(request));
    }

    // LİSTELEME – Tüm stok hareketleri (en yeniden eskiye)
    @GetMapping
    public ResponseEntity<List<StockMovement>> getAllMovements(
            @RequestParam(required = false) MovementType movementType) {
        if (movementType != null) {
            return ResponseEntity.ok(stockMovementService.getMovementsByType(movementType));
        }
        return ResponseEntity.ok(stockMovementService.getAllMovements());
    }

    // FİLTRELEME – Belirli bir ürüne ait hareketler
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<StockMovement>> getMovementsByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(stockMovementService.getMovementsByProductId(productId));
    }
}

