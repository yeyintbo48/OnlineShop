package com.online.shop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.online.shop.dtos.ProductDto;
import com.online.shop.entity.Product;

@Mapper(componentModel= "spring")
public interface ProductMapper {
    @Mapping(source = "category.id",target = "category_id")
    ProductDto toDto(Product product);

    @Mapping(source ="categoryId",target = "category.id")
    Product toEntity(ProductDto productDto);
}
