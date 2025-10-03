package com.example.pwm.config;
import org.springframework.context.annotation.*; import org.springframework.web.servlet.config.annotation.*;
@Configuration public class CorsConfig implements WebMvcConfigurer {
  @Override public void addCorsMappings(CorsRegistry reg){
    reg.addMapping("/**").allowedMethods("*").allowedOrigins("*").allowedHeaders("*"); // tighten for prod
  }
}
