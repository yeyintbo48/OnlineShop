package com.online.shop.dtos;

import java.math.BigDecimal;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductDto {
    private Long id;

    @NotBlank(message = "Name cannot be blank!")
    private String name;
     
    @NotBlank(message = "Description cannot be blank!")
    private String description;

    @NotNull(message = "Price cannot be Null!")
    @Min(value = 0,message = "At least Price is 0 or more!")
    private BigDecimal price;

    @Min(value = 0,message = "Stock quantity must be 0 or more!")
    private Integer stock_quantity;

    @NotNull(message = "Category id is required")
    private Long categoryId;
}