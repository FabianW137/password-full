package com.example.pwm.config;
import com.example.pwm.service.*; import org.springframework.context.annotation.*; 
@Configuration public class AppConfig {
  @Bean public CryptoService cryptoService(){ return new CryptoService(System.getenv().getOrDefault("APP_ENCRYPTION_KEY","")); }
  @Bean public JwtService jwtService(){ return new JwtService(System.getenv().getOrDefault("JWT_SECRET","")); }
}
