package com.ecomerce.sb_ecom.controller;
import com.ecomerce.sb_ecom.models.Category;
import com.ecomerce.sb_ecom.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api") // adds /api to all the api request made
public class categoryController {

//    @Autowired
//    private CategoryService categoryService;

//     OR

    private CategoryService categoryService;
    @Autowired
    public categoryController(CategoryService categoryService) { // dependency injection constructor
        this.categoryService = categoryService;
    }// finds a bean matching with CategoryService and automatically injects it

    @GetMapping("/public/categories")
    //or
    //@RequestMapping(value="/public/categories" ,method=RequestMethod.GET)
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories=categoryService.getAllCategories();
        return ResponseEntity.status(HttpStatus.OK).body(categories);
    }

    @PostMapping("/public/categories")
    public ResponseEntity<String> addCategory( @Valid @RequestBody Category category) { // along with request body request must be valid
        categoryService.createNewCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body("Category added successfully");
    }

    @DeleteMapping("/adimin/deleteCategory/{CategoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long CategoryId) {
            String response=categoryService.deleteCategory(CategoryId);
            //return new ResponseEntity<>(response, HttpStatus.OK);
            //return ResponseEntity.ok(response);
            return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/public/updateCategory/{CategoryId}")
    public ResponseEntity<String> updateCategory( @Valid @PathVariable Long CategoryId,@RequestBody Category category){
            String response = categoryService.updateCategory(CategoryId, category);
            return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
