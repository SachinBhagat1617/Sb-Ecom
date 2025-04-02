package com.ecomerce.sb_ecom.service;
import com.ecomerce.sb_ecom.exceptions.APIException;
import com.ecomerce.sb_ecom.exceptions.ResourceNotFoundException;
import com.ecomerce.sb_ecom.models.Category;
import com.ecomerce.sb_ecom.payload.CategoryDTO;
import com.ecomerce.sb_ecom.payload.CategoryResponse;
import com.ecomerce.sb_ecom.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

//- If `CategoryServiceImpl` implements `CategoryService`, Spring registers the bean under the
//        **interface name** (`CategoryService`) by default.

@Service  // declare it as a bean so that object can be created by spring ioc automatically
public class CategoryServiceImpl implements CategoryService {
    //private List<Category> categories=new ArrayList<>();

    private CategoryRepository categoryRepository;
    @Autowired // Injecting the CategoryRepository dependency using Constructor AUTOWIRED injection
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Autowired // field Injection
    private ModelMapper modelMapper; // fromAppConfig

    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize,String sortBy,String sortOrder) {
        Sort sortByAndOrder=sortOrder.equalsIgnoreCase("asc")?Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();
        Pageable pageDetails= PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Category> categoryPage=categoryRepository.findAll(pageDetails);
        //System.out.println("hii"+categoryPage);
        List<Category> categories=categoryPage.getContent();
        if(categories.isEmpty()){
            throw new APIException("No Category added till now !!!");
        }
        List<CategoryDTO> categoryDTOS=categories.stream()
                .map(category->modelMapper.map(category,CategoryDTO.class)).toList();
        CategoryResponse categoryResponse=new CategoryResponse();
        categoryResponse.setContent(categoryDTOS);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());
        return categoryResponse;
    }
    @Override
    public CategoryDTO createNewCategory(CategoryDTO categoryDTO) {
        //category.setId(nextId++);
        Category category=modelMapper.map(categoryDTO,Category.class);
        Category categoryFromDb=categoryRepository.findByCategoryName(category.getCategoryName());
        if(categoryFromDb!=null){
            throw new APIException("Category already exists");
        }
        Category savedCategory=categoryRepository.save(category);
        CategoryDTO savedCategoryDTo=modelMapper.map(savedCategory,CategoryDTO.class);
        return savedCategoryDTo;
    }
    @Override
    public CategoryDTO deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(()->new ResourceNotFoundException("Category","CategoryId",categoryId));
        categoryRepository.deleteById(categoryId);
//        List<Category> categories=categoryRepository.findAll();
//        Category category=categories.stream()
//                .filter(c->c.getId()==(categoryId))
//                .findFirst().orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Resource not found"));
//        categoryRepository.delete(category);

        CategoryDTO categoryDTO=modelMapper.map(category,CategoryDTO.class);
        return categoryDTO;
    }
    @Override
    public CategoryDTO updateCategory(Long categoryId,CategoryDTO categoryDTO) {
        Category category=modelMapper.map(categoryDTO,Category.class); // src, destination class
        Category existingCategory=categoryRepository.findById(categoryId).orElseThrow(()->new ResourceNotFoundException("Category","CategoryId",categoryId));
        category.setCategoryId(categoryId);
        Category savedCategory=categoryRepository.save(category);
//        List<Category> categories=categoryRepository.findAll();
//        Category existingCategory=categories.stream()
//                .filter(c->c.getId()==(categoryId))
//                .findFirst().orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Resource not found to Update"));
//        existingCategory.setName(category.getName());
//        categoryRepository.save(existingCategory);
        CategoryDTO savedCategoryDTO=modelMapper.map(savedCategory,CategoryDTO.class);
        return savedCategoryDTO;
    }
}
