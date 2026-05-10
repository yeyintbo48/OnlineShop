package com.online.shop.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.online.shop.entity.Cart;
import com.online.shop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<Cart> addItemsToCart(@RequestParam Long userId,@RequestParam Long productId,@RequestParam int quantity){
        Cart savedItem = cartService.addItemsToCart(userId,productId, quantity);
        return ResponseEntity.ok(savedItem);
    }

    @GetMapping
    public ResponseEntity<List<Cart>> getCartItems() {
        List<Cart> carts = cartService.getCartItems();
        return ResponseEntity.ok(carts);
    }

    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<String> cartItemRemove(@PathVariable Long cartItemId){
        cartService.cartItemRemove(cartItemId);
        return ResponseEntity.ok("Item removed from cart successfully!");
    }

    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<String> clearCart(@PathVariable Long userId){
        cartService.clearCart(userId);
        return ResponseEntity.ok("Cart cleared successfully!");
    }
}
