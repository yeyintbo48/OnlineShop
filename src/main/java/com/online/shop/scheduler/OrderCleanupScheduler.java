package com.online.shop.scheduler;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.online.shop.dtos.OrderStatus;
import com.online.shop.entity.Order;
import com.online.shop.entity.OrderItem;
import com.online.shop.entity.OrderStatusHistory;
import com.online.shop.entity.Product;
import com.online.shop.repository.OrderRepo;
import com.online.shop.repository.OrderStatusHistoryRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderCleanupScheduler {
    private final OrderRepo orderRepo;
    private final OrderStatusHistoryRepo historyRepo;

    //အချိန်ငါးမိနစ်ပြည့်တိုင်းဒီ method က auto အလုပ်လုပ်မယ်
    @Scheduled(fixedRate = 300000) //5minutes (with milliseconds)

    public void cancelUnpaidOrderAndRestoreStock(){
        log.info("🚩Starting Order Cleanup Order Scheduler...");

        LocalDateTime timeoutLimit = LocalDateTime.now().minusMinutes(30);

        List<Order> expiredOrders = orderRepo.findByOrderStatusAndOrderedAt(OrderStatus.PENDING,timeoutLimit);
        if(expiredOrders.isEmpty()){
            log.info("✅No expired not found!");
            return;
        }
        for(Order order : expiredOrders){
            cancelOrder(order);
        }
        log.info("Order cleanup finished.Cancelled {} order.",expiredOrders.size());
    }

    private void cancelOrder(Order order){
        OrderStatus oldStatus = order.getOrderStatus();
        OrderStatus newStatus = OrderStatus.CANCELLED;

        order.setOrderStatus(newStatus);
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setPreviousStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setRemarks("System Auto Cancellation: PaymentTimeout after 30 minutes.");
        historyRepo.save(history);

        for(OrderItem item : order.getOrderItems()){
            Product product  = item.getProduct();
            int restoredStock = product.getStock_quantity() + item.getQuantity();
            product.setStock_quantity(restoredStock);
        }
        log.info("OrderId:{} is auto cancelled.Stock restored.",order.getId());
    }
}
