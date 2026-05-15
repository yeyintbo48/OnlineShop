package com.online.shop.dtos;

import java.math.BigDecimal;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductDto {
    private Long id;

    @NotBlank(message = "You need to add product name!")
    private String name;
     
    @NotBlank(message = "Description cannot be blank!")
    private String description;

    @Min(value = 0,message = "price is not negative!")
    private BigDecimal price;

    @Min(value = 0,message = "Stock quantity is not negative!")
    private Integer stock_quantity;

    @NotNull(message = "Please select category")
    private Long categoryId;
}