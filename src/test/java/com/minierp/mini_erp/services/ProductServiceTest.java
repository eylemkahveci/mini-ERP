package com.minierp.mini_erp.services;

import com.minierp.mini_erp.entities.Product;
import com.minierp.mini_erp.exceptions.ResourceNotFoundException;
import com.minierp.mini_erp.repositories.CategoryRepository;
import com.minierp.mini_erp.repositories.ProductRepository;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @Mock
    CategoryRepository categoryRepository;

    @InjectMocks
    ProductService productService;

    @Test
    @DisplayName("deleteProduct çağrıldığında soft delete sonucu active=false olur")
    void deleteProduct_shouldSoftDeleteProduct() {
        Product p = new Product();
        p.setPId(1L);
        p.setActive(true);

        when(productRepository.findById(1L)).thenReturn(Optional.of(p));
        doAnswer(inv -> {
            Product arg = inv.getArgument(0);
            arg.setActive(false);
            return null;
        }).when(productRepository).delete(any(Product.class));

        productService.deleteProduct(1L);

        verify(productRepository).delete(p);
        assertFalse(p.isActive());
    }

    @Test
    @DisplayName("Silinecek ürün bulunamazsa ResourceNotFoundException fırlatılır")
    void deleteProduct_shouldThrowWhenNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(99L));
        verify(productRepository, never()).delete(any(Product.class));
    }

    @Test
    @DisplayName("Product üzerinde soft delete anotasyonları tanımlıdır")
    void productEntity_shouldHaveSoftDeleteAnnotations() {
        SQLDelete sqlDelete = Product.class.getAnnotation(SQLDelete.class);
        assertNotNull(sqlDelete);
        assertTrue(sqlDelete.sql().toLowerCase().contains("active = false"));

        Where where = Product.class.getAnnotation(Where.class);
        assertNotNull(where);
        assertTrue(where.clause().toLowerCase().contains("active = true"));
    }
}

