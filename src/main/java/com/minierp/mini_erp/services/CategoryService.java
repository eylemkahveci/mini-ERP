package com.minierp.mini_erp.services;

import com.minierp.mini_erp.entities.Category;
import com.minierp.mini_erp.repositories.CategoryRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // Constructor
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // CREATE - Yeni kategori kaydetme
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    // READ - Tüm kategorileri listeleme
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // READ - ID'ye göre kategori getirme
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kategori bulunamadı: " + id));
    }

    // UPDATE - Kategori güncelleme
    public Category updateCategory(Long id, Category updatedCategory) {
        Category existing = getCategoryById(id);
        existing.setName(updatedCategory.getName());
        return categoryRepository.save(existing);
    }

    // DELETE - Kategori silme
    public void deleteCategory(Long id) {
        Category existing = getCategoryById(id);
        categoryRepository.delete(existing);
    }
}