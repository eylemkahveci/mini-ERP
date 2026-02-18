package com.minierp.mini_erp.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class StockMovementRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Miktar -10 ise @Positive ihlali oluşur")
    void quantityNegative_shouldFailValidation() {
        StockMovementRequest req = new StockMovementRequest();
        req.setProductId(1L);
        req.setQuantity(-10);
        req.setDescription("negatif");

        Set<ConstraintViolation<StockMovementRequest>> violations = validator.validate(req);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> "quantity".equals(v.getPropertyPath().toString())));
    }

    @Test
    @DisplayName("Miktar 0 ise @Positive ihlali oluşur")
    void quantityZero_shouldFailValidation() {
        StockMovementRequest req = new StockMovementRequest();
        req.setProductId(1L);
        req.setQuantity(0);
        req.setDescription("sıfır");

        Set<ConstraintViolation<StockMovementRequest>> violations = validator.validate(req);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> "quantity".equals(v.getPropertyPath().toString())));
    }

    @Test
    @DisplayName("Geçerli miktar için ihlal oluşmaz")
    void quantityPositive_shouldPassValidation() {
        StockMovementRequest req = new StockMovementRequest();
        req.setProductId(1L);
        req.setQuantity(5);
        req.setDescription("geçerli");

        Set<ConstraintViolation<StockMovementRequest>> violations = validator.validate(req);
        assertTrue(violations.isEmpty());
    }
}

