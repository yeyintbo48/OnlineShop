package com.online.shop.controller;

import org.springframework.web.bind.annotation.RestController;
import com.online.shop.dtos.PaymentRequestDto;
import com.online.shop.service.PaymentService;
import com.stripe.model.Charge;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/charge")
    public ResponseEntity<String> chargeCard(@RequestBody PaymentRequestDto request) {
        try{
        Charge charge = paymentService.processPaymentAndUpdateOrder(request);
        if(charge.getPaid()){
            return ResponseEntity.ok("Payment Successfully with Transactional ID:" + charge.getId());
        }else{
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Payment Failed");
        }
        }catch(Exception e){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error:" + e.getMessage());
        }
    } 
}
