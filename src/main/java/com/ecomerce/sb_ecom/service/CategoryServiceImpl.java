package com.ecomerce.sb_ecom.service;
import com.ecomerce.sb_ecom.exceptions.APIException;
import com.ecomerce.sb_ecom.exceptions.ResourceNotFoundException;
import com.ecomerce.sb_ecom.models.Category;
import com.ecomerce.sb_ecom.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    public List<Category> getAllCategories() {
        List<Category>categories=categoryRepository.findAll();
        if(categories.isEmpty()){
            throw new APIException("No Category added till now !!!");
        }
        return categories;
    }
    @Override
    public void createNewCategory(Category category) {
        //category.setId(nextId++);
        Category savedCategory=categoryRepository.findByCategoryName(category.getCategoryName());
        if(savedCategory!=null){
            throw new APIException("Category already exists");
        }
        categoryRepository.save(category);
    }
    @Override
    public String deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(()->new ResourceNotFoundException("Category","CategoryId",categoryId));
        categoryRepository.deleteById(categoryId);
//        List<Category> categories=categoryRepository.findAll();
//        Category category=categories.stream()
//                .filter(c->c.getId()==(categoryId))
//                .findFirst().orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Resource not found"));
//        categoryRepository.delete(category);
        return "Category of categoryId "+categoryId+" deleted successfully";
    }
    @Override
    public String updateCategory(Long categoryId,Category category) {
        Category existingCategory=categoryRepository.findById(categoryId).orElseThrow(()->new ResourceNotFoundException("Category","CategoryId",categoryId));
        System.out.println(existingCategory);
        category.setCategoryId(categoryId);
        categoryRepository.save(category);
//        List<Category> categories=categoryRepository.findAll();
//        Category existingCategory=categories.stream()
//                .filter(c->c.getId()==(categoryId))
//                .findFirst().orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Resource not found to Update"));
//        existingCategory.setName(category.getName());
//        categoryRepository.save(existingCategory);
        return "Category of categoryId "+categoryId+" updated successfully";
    }
}
