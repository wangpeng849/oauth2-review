package com.review.userserverjwt.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author wangp
 * @Date 2020/5/7
 * @Version 1.0
 */
@RestController
@CrossOrigin(value = "*")
public class HelloController {

    @GetMapping("hello")
    public String hello() {
        return "hello";
    }

    @GetMapping("admin/hello")
    public String admin() {
        return "admin";
    }
}
