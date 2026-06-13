package com.online.shop.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.online.shop.entity.Order;

public interface OrderRepo extends JpaRepository<Order,Long>{
    Page<Order> findByUserId(Long userId,Pageable pageable);
}
