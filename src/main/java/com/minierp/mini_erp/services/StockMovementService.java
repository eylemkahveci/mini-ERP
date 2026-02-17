package com.minierp.mini_erp.services;

import com.minierp.mini_erp.dto.StockMovementRequest;
import com.minierp.mini_erp.entities.MovementType;
import com.minierp.mini_erp.entities.Product;
import com.minierp.mini_erp.entities.StockMovement;
import com.minierp.mini_erp.repositories.ProductRepository;
import com.minierp.mini_erp.repositories.StockMovementRepository;
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
                .orElseThrow(() -> new RuntimeException("Ürün bulunamadı: " + request.getProductId()));

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

        return stockMovementRepository.save(movement);
    }

    @Transactional
    public StockMovement createOutMovement(StockMovementRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Ürün bulunamadı: " + request.getProductId()));

        int currentQuantity = product.getQuantity();
        int newQuantity = currentQuantity - request.getQuantity();

        // Şimdilik basit bir kontrol: eğer stok eksiye düşüyorsa hata fırlat
        if (newQuantity < 0) {
            throw new RuntimeException("Yeterli stok yok. Mevcut stok: " + currentQuantity +
                    ", istenen çıkış: " + request.getQuantity());
        }

        product.setQuantity(newQuantity);
        productRepository.save(product);

        StockMovement movement = new StockMovement();
        movement.setProduct(product);
        movement.setMovementType(MovementType.OUT);
        movement.setQuantity(request.getQuantity());
        movement.setDescription(request.getDescription());

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
}

