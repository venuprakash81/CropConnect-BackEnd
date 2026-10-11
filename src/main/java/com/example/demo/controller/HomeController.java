package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {

        return "CropConnect Backend is Running Successfully on Port 8081";
    }

    @GetMapping("/api/test")
    public String test() {

        return "CropConnect API is Working";
    }
}