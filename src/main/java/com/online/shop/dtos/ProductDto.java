package com.online.shop.dtos;

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
    private Double price;

    @Min(message = "You need to buy at least one item", value = 1)
    private Integer stock_quantity;

    @NotNull(message = "Category id is required")
    private Long categoryId;
}