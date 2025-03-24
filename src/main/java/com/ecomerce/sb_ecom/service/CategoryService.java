package com.ecomerce.sb_ecom.service;

import com.ecomerce.sb_ecom.models.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getAllCategories();
    void createNewCategory(Category category);
    String deleteCategory(Long categoryId);
    String updateCategory(Long categoryId,Category category);
}
