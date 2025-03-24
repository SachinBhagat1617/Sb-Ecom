package com.ecomerce.sb_ecom.repositories;

import com.ecomerce.sb_ecom.models.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {//// <Type_of_entity, data_type_of_primary_key
    Category findByCategoryName(@NotBlank @Size(min=3,message = "Name must contain atleast 3 characters") String categoryName);  // creates new findByCategoryName automatically and will search in the DB
    // will automatically perform crud operations
}
