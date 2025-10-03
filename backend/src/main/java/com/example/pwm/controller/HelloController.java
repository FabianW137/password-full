package com.example.pwm.controller;
import org.springframework.web.bind.annotation.*; @RestController public class HelloController {
@GetMapping("/api/status") public String status(){ return "ok"; } }
