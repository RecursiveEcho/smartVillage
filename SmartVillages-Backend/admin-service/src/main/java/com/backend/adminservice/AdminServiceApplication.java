package com.backend.adminservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@MapperScan({"com.backend.admin.mapper", "com.backend.auth.mapper"})
@SpringBootApplication(
    scanBasePackages = {
      "com.backend.common",
      "com.backend.adminservice",
      "com.backend.admin"
    })
@EnableFeignClients(basePackages = "com.backend.admin.client")
@EnableDiscoveryClient
public class AdminServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(AdminServiceApplication.class, args);
  }
}
