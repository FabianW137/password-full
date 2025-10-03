package com.example.pwm;
import org.springframework.boot.autoconfigure.SpringBootApplication; import org.springframework.boot.SpringApplication;
import org.springframework.cache.annotation.EnableCaching;
@SpringBootApplication @EnableCaching public class BackendApplication{
  public static void main(String[] a){ SpringApplication.run(BackendApplication.class,a); }
}
