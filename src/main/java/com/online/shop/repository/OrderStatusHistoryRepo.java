package com.online.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.online.shop.entity.OrderStatusHistory;

public interface OrderStatusHistoryRepo extends JpaRepository<OrderStatusHistory,Long>{

}
