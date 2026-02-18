package com.minierp.mini_erp.services;

import com.minierp.mini_erp.dto.StockMovementRequest;
import com.minierp.mini_erp.entities.MovementType;
import com.minierp.mini_erp.entities.Product;
import com.minierp.mini_erp.entities.StockMovement;
import com.minierp.mini_erp.repositories.ProductRepository;
import com.minierp.mini_erp.repositories.StockMovementRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockMovementServiceTest {

    @Mock
    ProductRepository productRepository;

    @Mock
    StockMovementRepository stockMovementRepository;

    @InjectMocks
    StockMovementService stockMovementService;

    @Test
    @DisplayName("IN hareketinde stok artar ve hareket doğru kaydedilir")
    void createInMovement_shouldIncreaseStockAndSaveMovement() {
        Product product = new Product();
        product.setPId(1L);
        product.setQuantity(10);

        StockMovementRequest req = new StockMovementRequest();
        req.setProductId(1L);
        req.setQuantity(5);
        req.setDescription("Giriş");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));
        when(stockMovementRepository.save(any(StockMovement.class))).thenAnswer(inv -> inv.getArgument(0));

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        ArgumentCaptor<StockMovement> movementCaptor = ArgumentCaptor.forClass(StockMovement.class);

        StockMovement result = stockMovementService.createInMovement(req);

        verify(productRepository).save(productCaptor.capture());
        assertEquals(15, productCaptor.getValue().getQuantity());

        verify(stockMovementRepository).save(movementCaptor.capture());
        StockMovement saved = movementCaptor.getValue();
        assertEquals(MovementType.IN, saved.getMovementType());
        assertEquals(5, saved.getQuantity());
        assertSame(product, saved.getProduct());

        assertNotNull(result);
        assertEquals(MovementType.IN, result.getMovementType());
    }

    @Test
    @DisplayName("OUT hareketinde stok yeterliyse azalır ve hareket kaydedilir")
    void createOutMovement_shouldDecreaseStockAndSaveMovement() {
        Product product = new Product();
        product.setPId(2L);
        product.setQuantity(10);

        StockMovementRequest req = new StockMovementRequest();
        req.setProductId(2L);
        req.setQuantity(3);
        req.setDescription("Çıkış");

        when(productRepository.findById(2L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));
        when(stockMovementRepository.save(any(StockMovement.class))).thenAnswer(inv -> inv.getArgument(0));

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        ArgumentCaptor<StockMovement> movementCaptor = ArgumentCaptor.forClass(StockMovement.class);

        StockMovement result = stockMovementService.createOutMovement(req);

        verify(productRepository).save(productCaptor.capture());
        assertEquals(7, productCaptor.getValue().getQuantity());

        verify(stockMovementRepository).save(movementCaptor.capture());
        StockMovement saved = movementCaptor.getValue();
        assertEquals(MovementType.OUT, saved.getMovementType());
        assertEquals(3, saved.getQuantity());
        assertSame(product, saved.getProduct());

        assertNotNull(result);
        assertEquals(MovementType.OUT, result.getMovementType());
    }

    @Test
    @DisplayName("OUT hareketinde stok yetersizse hata fırlatılır ve kayıt yapılmaz")
    void createOutMovement_shouldThrowWhenInsufficientStock() {
        Product product = new Product();
        product.setPId(3L);
        product.setQuantity(2);

        StockMovementRequest req = new StockMovementRequest();
        req.setProductId(3L);
        req.setQuantity(5);
        req.setDescription("Yetersiz deneme");

        when(productRepository.findById(3L)).thenReturn(Optional.of(product));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> stockMovementService.createOutMovement(req));
        assertTrue(ex.getMessage().toLowerCase().contains("yetersiz stok"));

        verify(productRepository, never()).save(any(Product.class));
        verify(stockMovementRepository, never()).save(any(StockMovement.class));
    }
}

