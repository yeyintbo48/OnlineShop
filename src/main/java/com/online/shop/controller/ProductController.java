package com.online.shop.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.online.shop.dtos.ProductDto;
import com.online.shop.entity.Category;
import com.online.shop.entity.Product;
import com.online.shop.service.CategoryService;
import com.online.shop.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;
    private final CategoryService categoryService;

    @GetMapping
        public ResponseEntity<List<ProductDto>> getAllProducts() {
        List<ProductDto> productDtos = productService.getAllProducts()
            .stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
            return ResponseEntity.ok(productDtos);
    }

    @PostMapping("/add")
    public ResponseEntity<ProductDto> createProduct(@RequestBody @Valid ProductDto request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setDescription(request.getDescription());
        product.setStock_quantity(request.getStock_quantity());

        Category category = categoryService.getCategoryById((request.getCategoryId()));
        product.setCategory(category);

        Product savedProduct = productService.createProduct(product);
        return ResponseEntity.ok(mapToDto(savedProduct));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(mapToDto(product));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long  id, @RequestBody @Valid ProductDto request) {
        Product product = productService.updateProduct(id, request);
        return ResponseEntity.ok(mapToDto(product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id){
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    private ProductDto mapToDto(Product product){
        ProductDto dto = new ProductDto();
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setDescription(product.getDescription());
        dto.setStock_quantity(product.getStock_quantity());

        if(product.getCategory() != null){
            dto.setCategoryId(product.getCategory().getId());
        }
        return dto;
    }

    @GetMapping("/page")
    public ResponseEntity<Page<Product>> getAllProducts(
        @RequestParam Long userId,
        @RequestParam(defaultValue = "0")int page,
        @RequestParam(defaultValue = "10")int size) {
        Page<Product> products = productService.getUserProducts(userId,page,size);
        return ResponseEntity.ok(products);
    }  
}
