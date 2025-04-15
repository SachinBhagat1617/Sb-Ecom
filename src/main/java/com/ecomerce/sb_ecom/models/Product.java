package com.ecomerce.sb_ecom.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="products")
@ToString
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long productId;

    @NotBlank
    @Size(min = 3, message = "productName must be atleast of 3 chars")
    private String productName;

    @NotBlank
    private String image;

    @NotBlank
    @Size(min = 6, message = "productDescriptions must be atleast of 6 chars")
    private String description;

    @NotNull
    @Positive
    private Integer quantity;

    @NotNull(message = "Price cannot be null") // Ensures price is provided
    @Positive(message = "Price must be greater than zero") // Ensures price is positive
    private double price;

    private double discount;
    private double specialPrice;


    @ManyToOne
    @JoinColumn(name="category_id") // foreign Key
    private Category category;  //this is the owning side the side which contains foreign key and it has many to one relation ship

    @ManyToOne
    @JoinColumn(name="seller_id")
    private User user;


}
