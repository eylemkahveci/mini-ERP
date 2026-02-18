package com.minierp.mini_erp.repositories;

import com.minierp.mini_erp.entities.Category;
import com.minierp.mini_erp.entities.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:miniErpTest;DB_CLOSE_DELAY=-1;MODE=MySQL",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ProductRepositoryTest {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    TestEntityManager entityManager;

    @BeforeEach
    void cleanDatabase() {
        entityManager.getEntityManager().createQuery("delete from Product").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from Category").executeUpdate();
    }

    @Test
    @DisplayName("Kritik stok seviyesindeki ürünler doğru şekilde döner")
    void findLowStockProducts_shouldReturnExpected() {
        Category cat = new Category();
        cat.setName("Genel");
        entityManager.persist(cat);

        Product p1 = newProduct("Urun1", "SKU-1", 3, 5, cat);
        Product p2 = newProduct("Urun2", "SKU-2", 5, 5, cat);
        Product p3 = newProduct("Urun3", "SKU-3", 8, 5, cat);
        Product p4 = newProduct("Urun4", "SKU-4", 2, null, cat);

        entityManager.persist(p1);
        entityManager.persist(p2);
        entityManager.persist(p3);
        entityManager.persist(p4);
        entityManager.flush();

        List<Product> result = productRepository.findLowStockProducts();
        Set<String> skus = result.stream().map(Product::getSku).collect(Collectors.toSet());

        assertEquals(2, result.size());
        assertTrue(skus.contains("SKU-1"));
        assertTrue(skus.contains("SKU-2"));
        assertFalse(skus.contains("SKU-3"));
        assertFalse(skus.contains("SKU-4"));
    }

    private Product newProduct(String name, String sku, int qty, Integer critical, Category cat) {
        Product p = new Product();
        p.setName(name);
        p.setSku(sku);
        p.setPrice(BigDecimal.valueOf(100));
        p.setQuantity(qty);
        p.setCriticalStockLevel(critical);
        p.setCategory(cat);
        return p;
    }
}
