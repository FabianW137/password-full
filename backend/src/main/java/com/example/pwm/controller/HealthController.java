package com.example.pwm.controller;
import org.springframework.web.bind.annotation.*; @RestController public class HealthController { @GetMapping("/api/status") public String ok(){ return "ok"; } }
