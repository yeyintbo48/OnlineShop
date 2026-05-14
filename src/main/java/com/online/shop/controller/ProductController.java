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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductController {
    private final ProductService productService;
    private final CategoryService categoryService;

    @GetMapping
        public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PostMapping("/add")
    public ResponseEntity<Product> createProduct(@RequestBody @Valid ProductDto request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setDescription(request.getDescription());
        product.setStock_quantity(request.getStock_quantity());

        Category category = categoryService.getCategoryById((request.getCategoryId()));
        product.setCategory(category);

        Product savedProduct = productService.createProduct(product);
        return ResponseEntity.ok(savedProduct);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long  id, @RequestBody @Valid ProductDto request) {
        Product product = productService.updateProduct(id, request);
        return ResponseEntity.ok(product);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id){
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
