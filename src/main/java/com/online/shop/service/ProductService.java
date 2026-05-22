package com.online.shop.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.online.shop.dtos.ProductDto;
import com.online.shop.entity.Category;
import com.online.shop.entity.Product;
import com.online.shop.exception.ResourceNotFoundException;
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
        return productRepo.findById(id).orElseThrow(()-> new ResourceNotFoundException("Product id:" +id + "not found!"));
    }

    public Product updateProduct(Long id,ProductDto request){
        Product product = productRepo.findById(id).orElseThrow(()-> new ResourceNotFoundException("Product not found with ID:" + id)); 
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock_quantity(request.getStock_quantity());
        if(request.getCategoryId() != null){
            Category category = categoryRepo.findById(request.getCategoryId())
            .orElseThrow(()->new ResourceNotFoundException("Category not found with Id:" + request.getCategoryId()));
            product.setCategory(category);
        }
        return productRepo.save(product);
    }

    public void deleteProduct(Long id){
        Product product = getProductById(id);
        productRepo.delete(product);
    }

    @Transactional(readOnly = true)
    public Page<Product> getUserProducts(Long userId,int page,int size){
        Pageable pageable = PageRequest.of(page, size,Sort.by("id").descending());
        return productRepo.findByUserId(userId, pageable);
    }
}
