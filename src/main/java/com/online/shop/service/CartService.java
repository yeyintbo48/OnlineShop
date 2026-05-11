package com.online.shop.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.online.shop.entity.Cart;
import com.online.shop.entity.CartItem;
import com.online.shop.entity.Product;
import com.online.shop.entity.User;
import com.online.shop.exception.ResourceNotFoundException;
import com.online.shop.repository.CartItemRepo;
import com.online.shop.repository.CartRepo;
import com.online.shop.repository.ProductRepo;
import com.online.shop.repository.UserRepo;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartItemRepo cartItemRepo;
    private final ProductRepo productRepo;
    private final CartRepo cartRepo;
    private final UserRepo userRepo;

    public Cart addItemsToCart(Long userId,Long productId, int quantity) {
        Product product = productRepo.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        Cart cart = getOrCreateCart(userId);
        CartItem existingItem = cartItemRepo.findByCartAndProduct(cart,product);
        if (existingItem != null) {
            existingItem.setQuantity(quantity);
            cartItemRepo.save(existingItem);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cartItemRepo.save(newItem);
        }
        return cart;
    }
    
    public void cartItemRemove(Long cartItemId){
        CartItem cartItem = cartItemRepo.findById(cartItemId).orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        cartItemRepo.delete(cartItem);
    }

    public void clearCart(Long userId){
        Cart cart = cartRepo.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        cart.getCartItems().clear();
        cartRepo.save(cart);
    }

    public List<Cart> getCartItems(){
        return cartRepo.findAll();
    }

    public Cart getOrCreateCart(Long userId){
        return cartRepo.findByUserId(userId).orElseGet(() -> {
            User user = userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepo.save(newCart);
        });
    }
}
