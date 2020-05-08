package com.review.springsecuritywithjpa.dao;

import com.review.springsecuritywithjpa.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDao extends JpaRepository<User,Long> {
    User findUserByUsername(String username);
}