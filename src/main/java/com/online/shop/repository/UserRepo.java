package com.online.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.online.shop.entity.User;

@Repository
public interface UserRepo extends JpaRepository<User,Long>{

}
