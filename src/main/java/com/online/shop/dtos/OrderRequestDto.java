package com.online.shop.dtos;

import java.util.List;
import lombok.Data;

@Data
public class OrderRequestDto {
    private Long userId;
    private List<OrderItemRequestDto> items;
}
