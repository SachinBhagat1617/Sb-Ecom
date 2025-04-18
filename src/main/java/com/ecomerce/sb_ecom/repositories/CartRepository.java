package com.ecomerce.sb_ecom.repositories;

import com.ecomerce.sb_ecom.models.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


public interface CartRepository extends JpaRepository<Cart, Long> {
    @Query("Select c from Cart c where c.user.email=?1")
    Cart findCartByEmail(String s);
}
