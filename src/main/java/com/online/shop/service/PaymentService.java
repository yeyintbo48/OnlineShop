package com.online.shop.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.online.shop.dtos.OrderStatus;
import com.online.shop.dtos.PaymentRequestDto;
import com.online.shop.entity.Order;
import com.online.shop.entity.PaymentRecord;
import com.online.shop.repository.OrderRepo;
import com.online.shop.repository.PaymentRecordRepo;
import com.stripe.Stripe;
import com.stripe.model.Charge;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final OrderRepo orderRepo;
    private final PaymentRecordRepo paymentRecordRepo;

    @Value("${stripe.api.secretKey}")
    private String secretKey;

    @PostConstruct
    public void inti(){
        Stripe.apiKey = secretKey;
    }

    @Transactional
    public Charge processPaymentAndUpdateOrder(PaymentRequestDto request)throws Exception{
        Order order= orderRepo.findById(request.getOrderId()).orElseThrow(()-> new RuntimeException("Order not found!"));
        if(order.getOrderStatus() == OrderStatus.PAID){
            throw new Exception("Order Already Paided!");
        }
        Map<String,String> metadata = new HashMap<>();
        metadata.put("orderId", String.valueOf(order.getId()));

        Map<String,Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", request.getAmount());
        chargeParams.put("currency", request.getCurrency());
        chargeParams.put("source", request.getToken());
        chargeParams.put("metadata",metadata);

        Charge charge = Charge.create(chargeParams);

        if(charge.getPaid()){
            PaymentRecord record = new PaymentRecord();
            record.setId(order.getId());
            record.setTransactionId(charge.getId());
            record.setAmount(request.getAmount());
            record.setCurrency(request.getCurrency());
            record.setPaymentDate(LocalDateTime.now());
            paymentRecordRepo.save(record);

            order.setOrderStatus(OrderStatus.PAID);
            orderRepo.save(order);

            return charge;
        }else{
            order.setOrderStatus(OrderStatus.FAILED);
            orderRepo.save(order);
            throw new Exception("Payment failed at Stripe!");
        }
    }
}

