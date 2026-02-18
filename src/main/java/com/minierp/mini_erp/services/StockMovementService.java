package com.minierp.mini_erp.services;

import com.minierp.mini_erp.dto.StockMovementRequest;
import com.minierp.mini_erp.entities.MovementType;
import com.minierp.mini_erp.entities.Product;
import com.minierp.mini_erp.entities.StockMovement;
import com.minierp.mini_erp.exceptions.ResourceNotFoundException;
import com.minierp.mini_erp.repositories.ProductRepository;
import com.minierp.mini_erp.repositories.StockMovementRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StockMovementService {

    private final ProductRepository productRepository;
    private final StockMovementRepository stockMovementRepository;

    public StockMovementService(ProductRepository productRepository,
                                StockMovementRepository stockMovementRepository) {
        this.productRepository = productRepository;
        this.stockMovementRepository = stockMovementRepository;
    }

    @Transactional
    public StockMovement createInMovement(StockMovementRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Ürün bulunamadı: " + request.getProductId()));

        // Stok artır
        int newQuantity = product.getQuantity() + request.getQuantity();
        product.setQuantity(newQuantity);
        productRepository.save(product);

        // Hareket kaydını oluştur
        StockMovement movement = new StockMovement();
        movement.setProduct(product);
        movement.setMovementType(MovementType.IN);
        movement.setQuantity(request.getQuantity());
        movement.setDescription(request.getDescription());
        movement.setCreatedBy(getCurrentUsername());

        return stockMovementRepository.save(movement);
    }

    @Transactional
    public StockMovement createOutMovement(StockMovementRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Ürün bulunamadı: " + request.getProductId()));

        int currentStock = product.getQuantity();
        int requestedAmount = request.getQuantity();

        // Stok yetmiyorsa detaylı hata
        if (currentStock < requestedAmount) {
            throw new RuntimeException("Yetersiz stok! Mevcut: " + currentStock + ", İstenen: " + requestedAmount);
        }

        int newQuantity = currentStock - requestedAmount;
        product.setQuantity(newQuantity);
        productRepository.save(product);

        StockMovement movement = new StockMovement();
        movement.setProduct(product);
        movement.setMovementType(MovementType.OUT);
        movement.setQuantity(request.getQuantity());
        movement.setDescription(request.getDescription());
        movement.setCreatedBy(getCurrentUsername());

        return stockMovementRepository.save(movement);
    }

    /** Tüm stok hareketlerini en yeniden eskiye listele */
    public List<StockMovement> getAllMovements() {
        return stockMovementRepository.findAllByOrderByMovementDateDesc();
    }

    /** Belirli bir ürüne ait hareketleri en yeniden eskiye listele */
    public List<StockMovement> getMovementsByProductId(Long productId) {
        return stockMovementRepository.findByProductIdOrderByMovementDateDesc(productId);
    }

    /** Hareket tipine göre (IN veya OUT) listele */
    public List<StockMovement> getMovementsByType(MovementType movementType) {
        return stockMovementRepository.findByMovementTypeOrderByMovementDateDesc(movementType);
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            return "SYSTEM";
        }
        return auth.getName();
    }
}

