package com.online.shop.controller;

import com.online.shop.repository.OrderRepo;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.online.shop.dtos.OrderStatus;
import com.online.shop.entity.Order;
import com.online.shop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderRepo orderRepo;
    private final OrderService orderService;

    @PostMapping("/place/{userId}")
    public ResponseEntity<Order> placeOrder(@PathVariable Long userId){
        return ResponseEntity.ok(orderService.placeOrder(userId));
    }

    @GetMapping("/page")
    public ResponseEntity<Page<Order>> getUserOrders(
        @RequestParam Long userId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size){
        Page<Order> orders = orderService.getUserOrders(userId, page, size);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}/orderStatus")
    public ResponseEntity<Map<String,String>> updateOrderStatus(@PathVariable Long orderId) {
        Optional<Order> orderOptional = orderRepo.findById(orderId);
        if(orderOptional.isPresent()){
            Order order = orderOptional.get();
            Map<String ,String> response = new HashMap<>();
            response.put("status",order.getOrderStatus().toString());
            return ResponseEntity.ok(response);
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PatchMapping("/{orderId}/orderStatus")
    public ResponseEntity<Order> updateOrderStatus(
        @PathVariable Long orderId,
        @RequestParam OrderStatus newStatus,
        @RequestParam(required = false)String transactionId,
        @RequestParam(required = false)String remarks
    ){
        Order updatedOrder = orderService.updateOrderStatus(orderId, newStatus, transactionId, remarks);
        return ResponseEntity.ok(updatedOrder);
    }
}
