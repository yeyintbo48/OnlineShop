package com.online.shop.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.online.shop.entity.Order;

@Repository
public interface OrderRepo extends JpaRepository<Order,Long>{
    Page<Order> findByUserId(Long userId,Pageable pageable);
}
