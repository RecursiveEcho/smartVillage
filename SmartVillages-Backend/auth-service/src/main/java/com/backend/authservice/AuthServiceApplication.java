package com.backend.authservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan({"com.backend.auth.mapper", "com.backend.admin.mapper"})
@SpringBootApplication(
    scanBasePackages = {
      "com.backend.common",
      "com.backend.auth",
      "com.backend.admin",
      "com.backend.authservice"
    })
public class AuthServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(AuthServiceApplication.class, args);
  }
}
