package com.online.shop.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.online.shop.entity.Cart;

public interface CartRepo extends JpaRepository<Cart,Long>{
    Optional<Cart> findByUserId(Long userId);
}
