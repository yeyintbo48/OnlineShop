package com.online.shop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.online.shop.service.OrderService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/place")
    public ResponseEntity<?> placeOrder(@RequestParam Long userId){
        try{
            orderService.placeOrder(userId);
            return ResponseEntity.ok("Order placed successfully!");
        }catch(RuntimeException e){
            //not enough stock or cart is empty!
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
