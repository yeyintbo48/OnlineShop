package com.online.shop.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.online.shop.dtos.OrderStatus;
import com.online.shop.entity.Cart;
import com.online.shop.entity.CartItem;
import com.online.shop.entity.Order;
import com.online.shop.entity.OrderItem;
import com.online.shop.entity.OrderStatusHistory;
import com.online.shop.entity.PaymentRecord;
import com.online.shop.entity.Product;
import com.online.shop.exception.OrderProcessingException;
import com.online.shop.exception.ResourceNotFoundException;
import com.online.shop.repository.CartRepo;
import com.online.shop.repository.OrderRepo;
import com.online.shop.repository.OrderStatusHistoryRepo;
import com.online.shop.repository.PaymentRecordRepo;
import com.online.shop.repository.ProductRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {
    private final OrderRepo orderRepo;
    private final CartRepo cartRepo;
    private final ProductRepo productRepo;
    private final CartService cartService;
    private final PaymentRecordRepo paymentRecordRepo;
    private final OrderStatusHistoryRepo historyRepo;

    public Order placeOrder(Long userId){
        Cart cart = getCart(userId);
        Map<Long,Product> lockedProductMap = validateAndLockStock(cart);
        Order order = createOrder(userId,cart);
        saveOrderItems(order,cart,lockedProductMap);
        updateInventory(cart,lockedProductMap);
        cartService.clearCart(userId);
        return orderRepo.save(order);
    }

    private Cart getCart(Long userId){
        Cart cart = cartRepo.findByUserId(userId).orElseThrow(()-> new ResourceNotFoundException("Cart not found!"));
        if(cart.getCartItems().isEmpty()){
            throw new OrderProcessingException("Cart is empty!");
        }
        return cart;
    }

    private Map<Long,Product> validateAndLockStock(Cart cart){
        Map<Long,Product> lockedProducts = new HashMap<>();
        for(CartItem item : cart.getCartItems()){
            Product product = productRepo.findByIdForUpdate(item.getProduct().getId())
            .orElseThrow(() -> new ResourceNotFoundException("Product not found: "));
            if(product.getStock_quantity() <= 0){
                throw new OrderProcessingException(product.getName() + "Stock ပြတ်နေပါသည်! Available: " + product.getStock_quantity());
            }else if(item.getQuantity() > product.getStock_quantity()){
                throw new OrderProcessingException(
                    String.format("%s ရဲ့ လက်ကျန် Stock အရေအတွက်မှာ %d ခုသာရှိပါတယ်! သင်မှာ %d ခုလိုချင်ပါတယ်!",
                     product.getName(), product.getStock_quantity(), item.getQuantity())
                );
            }
            lockedProducts.put(product.getId(),product);
        }
        return lockedProducts;
    }

    private Order createOrder(Long userId,Cart cart){
        BigDecimal totalAmount  = cart.getCartItems()
            .stream().map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO,BigDecimal::add);

        Order order = new Order();
        order.setUser(cart.getUser());
        order.setTotalAmount(totalAmount);
        order.setOrderedAt(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.PENDING);
        return order;
    }

    private void saveOrderItems(Order order,Cart cart,Map<Long,Product> lockedProductMap){
        List<OrderItem> orderItems = cart.getCartItems()
            .stream().map(cartItem ->{
            Product lockedProduct = lockedProductMap.get(cartItem.getProduct().getId());

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());

            //စျေးနှုန်း SnapShot ကို lock ခတ်ထားတဲ့ product ဆီကဘဲယူမှာ
            orderItem.setPriceAtOrder(lockedProduct.getPrice());
            return orderItem;
        })
        .collect(Collectors.toList());
        order.setOrderItems(orderItems);
    }

    private void updateInventory(Cart cart, Map<Long,Product> lockedProductMap){
        for(CartItem item :cart.getCartItems()){
            Product lockedProduct = lockedProductMap.get(item.getProduct().getId());
            int newStock = lockedProduct.getStock_quantity() - item.getQuantity();
            lockedProduct.setStock_quantity(newStock);
        }
    }

    @Transactional(readOnly = true)
    public Page<Order> getUserOrders(Long userId,int page,int size){
        Pageable pageable = PageRequest.of(page, size,Sort.by("id").descending());
        return orderRepo.findByUserId(userId, pageable);
    }

    public Order updateOrderStatus(Long orderId,OrderStatus newStatus,String transactionId,String remarks){
        Order order = orderRepo.findById(orderId).orElseThrow(()->new ResourceNotFoundException("Order not found with ID:" + orderId));

        OrderStatus oldStatus = order.getOrderStatus();
        if(oldStatus == newStatus){
            log.info("Order {} is already {}.Ignoring duplicate update",orderId,newStatus);
            return order;
        }

        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setPreviousStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setRemarks(remarks);
        historyRepo.save(history);

        order.setOrderStatus(newStatus);
        orderRepo.save(order);

        if(newStatus == OrderStatus.PAID && transactionId != null && !transactionId.isEmpty()){
            Optional<PaymentRecord> existingRecord = paymentRecordRepo.findByTransactionId(transactionId);
            if(existingRecord.isEmpty()){
                PaymentRecord record = new PaymentRecord();
                record.setOrderId(orderId);
                record.setTransactionId(transactionId);
                record.setAmount(order.getTotalAmount());
                record.setCurrency("usd");
                record.setPaymentDate(LocalDateTime.now());
                paymentRecordRepo.save(record);
                log.info("Payment record saved for order id:{}",orderId);
            }
        }
        return order;
    }
}
