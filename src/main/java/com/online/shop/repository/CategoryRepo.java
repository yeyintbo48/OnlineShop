package com.online.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.online.shop.entity.Category;

@Repository
public interface CategoryRepo extends JpaRepository<Category,Long>{

}
