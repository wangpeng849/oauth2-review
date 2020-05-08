package com.review.springsecuritywithjpa;

import com.review.springsecuritywithjpa.dao.UserDao;
import com.review.springsecuritywithjpa.entity.Role;
import com.review.springsecuritywithjpa.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class SpringSecurityWithjpaApplicationTests {


    @Autowired
    private UserDao userDao;

    @Test
    void contextLoads() {
        User user = new User();
        user.setUsername("wangp");
        user.setPassword("123");
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);
        List<Role> rs1 = new ArrayList<>();
        Role r1 = new Role();
        r1.setName("ROLE_admin");
        r1.setNameZh("管理员");
        rs1.add(r1);
        user.setRoles(rs1);
        userDao.save(user);


        User user2 = new User();
        user2.setUsername("farling");
        user2.setPassword("123");
        user2.setAccountNonExpired(true);
        user2.setAccountNonLocked(true);
        user2.setCredentialsNonExpired(true);
        user2.setEnabled(true);
        List<Role> rs2 = new ArrayList<>();
        Role r2 = new Role();
        r2.setName("ROLE_user");
        r2.setNameZh("普通用户");
        rs2.add(r2);
        user2.setRoles(rs2);
        userDao.save(user2);
    }

}
