package com.ecomerce.sb_ecom.repositories;

import com.ecomerce.sb_ecom.models.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Query("SELECT C FROM CartItem C WHERE C.cart.cartId=?1 AND C.product.productId=?2")
    CartItem findCartItemByProductIdAndCartId(Long cartId, Long productId);
}
