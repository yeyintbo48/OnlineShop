package com.online.shop.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.online.shop.dtos.CategoryDto;
import com.online.shop.entity.Category;
import com.online.shop.entity.Product;
import com.online.shop.repository.CategoryRepo;
import com.online.shop.repository.ProductRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepo categoryRepo;
    private final ProductRepo productRepo;

    public List<Category> getAllCategories(){
        return categoryRepo.findAll();
    }

    public Category getCategoryById(Long id){
        return categoryRepo.findById(id).orElseThrow(()-> new RuntimeException("Category not found with ID:" + id));
    }

    @Transactional
    public Category createCategory(CategoryDto request){
        Category category = new Category();
        category.setName(request.getName());
        if(request.getProducts() != null && !request.getProducts().isEmpty()){
            List<Product> products = productRepo.findAllById(request.getProducts());
            for(Product product : products){
                product.setCategory(category);
            }
            category.setProducts(products);
        }
        return categoryRepo.save(category);
    }

    public Category updateCategory(Long id,CategoryDto request){
        Category category = categoryRepo.findById(request.getId())
        .orElseThrow(()-> new RuntimeException("Category not found with ID:" + id));
        if(request.getProducts()!=null){
            List<Product> products = productRepo.findAllById(request.getProducts());
            products.forEach(p-> p.setCategory(category));
            category.setProducts(products);
        }
        return categoryRepo.save(category);
    }

    public void deleteCategory(Long id){
        Category category = getCategoryById(id);
        categoryRepo.delete(category);
    }
}
