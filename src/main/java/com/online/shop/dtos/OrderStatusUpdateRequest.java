package com.online.shop.dtos;

import jakarta.validation.constraints.NotNull;

public record OrderStatusUpdateRequest(
    @NotNull(message = "OrderStatus is required!")
    OrderStatus status,
    String remarks //Optional
) {}
