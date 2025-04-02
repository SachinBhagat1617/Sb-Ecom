package com.ecomerce.sb_ecom.controller;
import com.ecomerce.sb_ecom.config.AppConstant;
import com.ecomerce.sb_ecom.models.Category;
import com.ecomerce.sb_ecom.payload.CategoryDTO;
import com.ecomerce.sb_ecom.payload.CategoryResponse;
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
    public ResponseEntity<CategoryResponse> getAllCategories(
            @RequestParam(name="pageNumber",defaultValue = AppConstant.PAGE_NUMBER,required = false) Integer pageNumber,
            @RequestParam(name="pageSize",defaultValue = AppConstant.PAGE_SIZE,required = false) Integer pageSize,
            @RequestParam(name="sortBy",defaultValue=AppConstant.SORT_BY,required=false) String sortBy,
            @RequestParam(name="sortOrder",defaultValue=AppConstant.SORT_ORDER,required=false) String sortOrder
    ) {
        CategoryResponse categoryResponse=categoryService.getAllCategories(pageNumber,pageSize,sortBy,sortOrder);
        return ResponseEntity.status(HttpStatus.OK).body(categoryResponse);
    }

    @PostMapping("/public/categories")
    public ResponseEntity<CategoryDTO> addCategory(@Valid @RequestBody CategoryDTO categoryDTO) { // along with request body request must be valid
        CategoryDTO savedCategoryDTO=categoryService.createNewCategory(categoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategoryDTO);
    }

    @DeleteMapping("/adimin/deleteCategory/{CategoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long CategoryId) {
            CategoryDTO categoryDTO =categoryService.deleteCategory(CategoryId);
            //return new ResponseEntity<>(response, HttpStatus.OK);
            //return ResponseEntity.ok(response);
            return ResponseEntity.status(HttpStatus.OK).body(categoryDTO);
    }

    @PutMapping("/public/updateCategory/{CategoryId}")
    public ResponseEntity<CategoryDTO> updateCategory( @Valid @PathVariable Long CategoryId,@RequestBody CategoryDTO categoryDTO){
           CategoryDTO UpdatedCategoryDTO = categoryService.updateCategory(CategoryId, categoryDTO);
            return ResponseEntity.status(HttpStatus.OK).body(UpdatedCategoryDTO);
    }

}
