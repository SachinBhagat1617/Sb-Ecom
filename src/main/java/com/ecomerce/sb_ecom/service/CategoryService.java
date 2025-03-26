package com.ecomerce.sb_ecom.service;

import com.ecomerce.sb_ecom.models.Category;
import com.ecomerce.sb_ecom.payload.CategoryDTO;
import com.ecomerce.sb_ecom.payload.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize,String sortBy,String sortOrder);
    CategoryDTO createNewCategory(CategoryDTO categoryDTO);
    CategoryDTO deleteCategory(Long categoryId);
    CategoryDTO updateCategory(Long categoryId,CategoryDTO categoryDTO);
}
