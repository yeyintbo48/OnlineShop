package com.online.shop.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.online.shop.dtos.OrderStatus;
import com.online.shop.entity.Cart;
import com.online.shop.entity.CartItem;
import com.online.shop.entity.Order;
import com.online.shop.entity.OrderItem;
import com.online.shop.entity.Product;
import com.online.shop.exception.OrderProcessingException;
import com.online.shop.exception.ResourceNotFoundException;
import com.online.shop.repository.CartRepo;
import com.online.shop.repository.OrderRepo;
import com.online.shop.repository.ProductRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final OrderRepo orderRepo;
    private final CartRepo cartRepo;
    private final ProductRepo productRepo;
    private final CartService cartService;

    public Order placeOrder(Long userId){
        Cart cart = getCart(userId);
        checkStock(cart);
        Order order = createOrder(userId,cart);
        saveOrderItems(order,cart);
        updateInventory(cart);
        cartService.clearCart(userId);
        return orderRepo.save(order);
    }

    private Cart getCart(Long userId){
        Cart cart = cartRepo.findById(userId).orElseThrow(()-> new ResourceNotFoundException("Cart not found!"));
        if(cart.getCartItems().isEmpty()){
            throw new OrderProcessingException("Cart is empty!");
        }
        return cart;
    }

    private void checkStock(Cart cart){
        for(CartItem item : cart.getCartItems()){
            Product product = item.getProduct();
            int requestedQty = item.getQuantity();
            int availableQty = product.getStock_quantity();
            if(requestedQty > availableQty){
                throw new OrderProcessingException(product.getName() + "Stock မလောက်ပါ။ကျန်ရှိမှု" + availableQty + "ခု");
            }
        }
    }

    private Order createOrder(Long userId,Cart cart){
        BigDecimal totalAmount  = cart.getCartItems().stream()
            .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO,BigDecimal::add);

        Order order = new Order();
        order.setUser(cart.getUser());
        order.setTotalAmount(totalAmount);
        order.setOrderedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        return order;
    }

    private void saveOrderItems(Order order,Cart cart){
        List<OrderItem> orderItems = cart.getCartItems().stream().map(cartItem ->{
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            //orderတင်တဲ့အချိန်priceကိုsnapshotသိမ်းထား
            orderItem.setPriceAtOrder(cartItem.getProduct().getPrice());
            return orderItem;
        })
        .collect(Collectors.toList());
        order.setOrderItems(orderItems);
    }

    private void updateInventory(Cart cart){
        for(CartItem item :cart.getCartItems()){
            Product product = item.getProduct();
            int newStock = product.getStock_quantity() - item.getQuantity();
            product.setStock_quantity(newStock);
            productRepo.save(product);
        }
    }

    public Page<Order> getUserOrders(Long userId,int page,int size){
        Pageable pageable = PageRequest.of(page, size,Sort.by("id").descending());
        return orderRepo.findByUserId(userId, pageable);
    }
}
