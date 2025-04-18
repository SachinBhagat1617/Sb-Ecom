package com.ecomerce.sb_ecom.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name="cart_items")
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartItemId;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart; // many to one-> there cane multiple product link to same cart_id

    @ManyToOne
    @JoinColumn(name="product_id")
    private Product product;//many to one-> there can be different user link to  same product


    private double discount;
    private double product_price;
    private int quantity;

}
// cart_item contain individual item
/*
* cart_item_id | cart_id | product_id | quantity
    1          | 101     | 1          | 2
    2          | 101     | 3          | 1
    3          | 102     | 1          | 1
    4          | 103     | 1          | 3
* */
