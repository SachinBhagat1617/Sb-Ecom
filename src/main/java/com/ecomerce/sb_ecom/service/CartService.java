package com.ecomerce.sb_ecom.service;

import com.ecomerce.sb_ecom.payload.CartDTO;
import jakarta.transaction.Transactional;

import java.util.List;

public interface CartService {
    CartDTO addProductToCart(Long productId, Integer quantity);

    List<CartDTO> getAllCarts();

    CartDTO getUserCart();

    @Transactional
    CartDTO updateProductQuantityInCart(Long productId, int operations);
}
