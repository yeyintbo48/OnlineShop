package com.online.shop.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.online.shop.dtos.ProductDto;
import com.online.shop.entity.Category;
import com.online.shop.entity.Product;
import com.online.shop.repository.CategoryRepo;
import com.online.shop.repository.ProductRepo;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepo productRepo;
    private final CategoryRepo categoryRepo;

    public List<Product> getAllProducts(){
        return productRepo.findAll();
    }

    public Product createProduct(Product product){
        return productRepo.save(product);
    }

    public Product getProductById(Long id){
        return productRepo.findById(id).orElseThrow(()-> new RuntimeException("Product not found with ID:" + id));
    }

    public Product updateProduct(Long id,ProductDto request){
        Product product = productRepo.findById(id).orElseThrow(()-> new RuntimeException("Product not found with ID:" + id)); 
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock_quantity(request.getStock_quantity());
        if(request.getCategoryId() != null){
            Category category = categoryRepo.findById(request.getCategoryId())
            .orElseThrow(()->new RuntimeException("Category not found with Id:" + request.getCategoryId()));
            product.setCategory(category);
        }
        return productRepo.save(product);
    }

    public void deleteProduct(Long id){
        Product product = getProductById(id);
        productRepo.delete(product);
    }
}
