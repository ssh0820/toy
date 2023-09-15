package com.example.toy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequiredArgsConstructor
@ComponentScan(basePackages = "com")
@SpringBootApplication(scanBasePackages = {"com.example.toy"})
public class AccountController {

    @GetMapping("/login")
    public String login(){
        return "account/login";
    }

    @GetMapping("/")
    public String index(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return "index";
    }
}
