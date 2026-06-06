package com.online.shop.dtos;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class PaymentRequestDto {
    private Long orderId;
    private BigDecimal amount;
    private String currency;
    private String token;
}
