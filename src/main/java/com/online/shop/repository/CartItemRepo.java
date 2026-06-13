package com.online.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.online.shop.entity.Cart;
import com.online.shop.entity.CartItem;
import com.online.shop.entity.Product;

public interface CartItemRepo extends JpaRepository<CartItem,Long>{
    CartItem findByProductId(Long productId);
    CartItem findByCartAndProduct(Cart cart,Product product);
}
