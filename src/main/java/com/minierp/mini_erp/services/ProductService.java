package com.minierp.mini_erp.services;

import com.minierp.mini_erp.dto.ProductDTO;
import com.minierp.mini_erp.entities.Category;
import com.minierp.mini_erp.entities.Product;
import com.minierp.mini_erp.repositories.CategoryRepository;
import com.minierp.mini_erp.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    // CREATE
    public Product createProduct(ProductDTO dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Kategori bulunamadı: " + dto.getCategoryId()));

        Product product = new Product();
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());
        product.setCategory(category);

        return productRepository.save(product);
    }

    // READ - Tüm ürünler
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // READ - ID'ye göre
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ürün bulunamadı: " + id));
    }

    // UPDATE
    public Product updateProduct(Long id, ProductDTO dto) {
        Product existing = getProductById(id);

        existing.setName(dto.getName());
        existing.setPrice(dto.getPrice());
        existing.setQuantity(dto.getQuantity());

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Kategori bulunamadı: " + dto.getCategoryId()));
            existing.setCategory(category);
        }

        return productRepository.save(existing);
    }

    // DELETE
    public void deleteProduct(Long id) {
        Product existing = getProductById(id);
        productRepository.delete(existing);
    }
}