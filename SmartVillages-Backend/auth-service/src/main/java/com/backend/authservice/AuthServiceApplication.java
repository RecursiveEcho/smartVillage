package com.backend.authservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@MapperScan({"com.backend.auth.mapper"})
@SpringBootApplication(
    scanBasePackages = {
      "com.backend.common",
      "com.backend.auth",
      "com.backend.authservice"
    })
@EnableDiscoveryClient
public class AuthServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(AuthServiceApplication.class, args);
  }
}
