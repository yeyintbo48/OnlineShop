package com.online.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.online.shop.entity.Category;

public interface CategoryRepo extends JpaRepository<Category,Long>{

}
