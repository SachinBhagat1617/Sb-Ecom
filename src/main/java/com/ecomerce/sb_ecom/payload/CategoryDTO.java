package com.ecomerce.sb_ecom.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {  // used to form DTO object when send request from client to server
    // represents category at the presentation layer and Category which is at the model represents category Entity at DB level
    private Long categoryId;
    private String categoryName;
}
