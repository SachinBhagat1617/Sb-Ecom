package com.ecomerce.sb_ecom.payload;

import com.ecomerce.sb_ecom.models.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private Long productId;
    private String productName;
    private String image;
    private String description;
    private Integer Quantity;
    private Double price;
    private Double discount;
    private Double specialPrice;

}
