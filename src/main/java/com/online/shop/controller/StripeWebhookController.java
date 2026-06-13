package com.online.shop.controller;

import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.online.shop.dtos.OrderStatus;
import com.online.shop.service.OrderService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Charge;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/webhook")
public class StripeWebhookController {
    private final OrderService orderService;

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @PostMapping
    public ResponseEntity<String> handleStripeEvents(
        @RequestBody byte[] payloadBytes,
        @RequestHeader("Stripe-Signature") String sigHeader) {
        System.out.println("Webhook Endpoint Hit! Received Request.");
        String payload = new String(payloadBytes,StandardCharsets.UTF_8);
        Event event = null;

        //Security Checking
        try{
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        }catch(SignatureVerificationException e){
            System.err.println("SignatureVerificationException:" + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Signature");
        }catch(Exception e){
            System.err.println("Exception in webhook:" + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Webhook Error!");
        }

        //Fetch Event Data
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        if(!dataObjectDeserializer.getObject().isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to deserialize object!");
        }

        switch(event.getType()){
            case "payment_intent.succeeded":
                PaymentIntent paymentIntent = (PaymentIntent) dataObjectDeserializer.getObject().get();
                String piOrderIdStr = paymentIntent.getMetadata().get("orderId");
                if(piOrderIdStr != null){
                    Long orderId = Long.parseLong(piOrderIdStr);
                    String remarks = "Payment Succeeded via Stripe PaymentIntent:" + paymentIntent.getId();
                    orderService.updateOrderStatus(orderId,OrderStatus.PAID,paymentIntent.getId(),remarks);
                    log.info("✅SUCCESS: Order: {} is marked as PAID",orderId);
                } else {
                    log.warn("⚠️ WARNING: payment_intent.succeeded ဝင်လာသော်လည်း Metadata တွင် 'orderId' မပါဝင်ပါ။ PaymentIntent ID: {}", paymentIntent.getId());
                }
                break;

            case "charge.succeeded":
                Charge successfulCharge = (Charge) dataObjectDeserializer.getObject().get();
                String orderIdStr  = successfulCharge.getMetadata().get("orderId");
                if(orderIdStr != null){
                    Long orderId = Long.parseLong(orderIdStr);
                    String remarks = "Payment Succeeded via Stripe Charge:" +successfulCharge.getId();
                    orderService.updateOrderStatus(orderId,OrderStatus.PAID,successfulCharge.getId(),remarks);
                    log.info("✅ SUCCESS: Order:{} is marked as PAID.",orderId);
                } else {
                    log.warn("⚠️ WARNING: charge.succeeded ဝင်လာသော်လည်း Metadata တွင် 'orderId' မပါဝင်ပါ။PaymentIntent ID:{}" ,successfulCharge.getId());
                }
                break;

                case "charge.failed":
                Charge failedCharge = (Charge) dataObjectDeserializer.getObject().get();
                String failedOrderIdStr  = failedCharge.getMetadata().get("orderId");
                if(failedOrderIdStr != null){
                    Long orderId = Long.parseLong(failedOrderIdStr);
                    String failReason = failedCharge.getFailureMessage() != null ? failedCharge.getFailureMessage() : "Unknown Reason";
                    String remarks = "Payment failed via Stripe Charge:" + failReason;
                    orderService.updateOrderStatus(orderId,OrderStatus.FAILED,failedCharge.getId(),remarks);
                    log.info("✅ FAILED: Order:{} is marked as FAIL.",orderId,failReason);
                } else {
                    log.warn("⚠️ WARNING: charge.failed ဝင်လာသော်လည်း Metadata တွင် 'orderId' မပါဝင်ပါ။Charge ID:{}", failedCharge.getId());
                }
                break;

                default :
                log.info("!INFO: Unhandled EventType:{}" + event.getType());
        }
        return ResponseEntity.ok("Success");
    }
}
