package com.online.shop.dtos;

import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CategoryDto {
    private Long id;

    @NotBlank(message = "Category name is required!")
    private String name;

    @NotEmpty(message = "Product list cannot be empty!")
    private List<Long> products;
}
