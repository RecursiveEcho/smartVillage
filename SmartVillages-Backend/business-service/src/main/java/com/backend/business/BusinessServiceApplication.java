package com.backend.business;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@MapperScan({
  "com.backend.announcement.mapper",
  "com.backend.feature.mapper",
  "com.backend.interaction.mapper",
  "com.backend.management.mapper"
})
@SpringBootApplication(
    scanBasePackages = {
      "com.backend.common",
      "com.backend.announcement",
      "com.backend.feature",
      "com.backend.interaction",
      "com.backend.management",
      "com.backend.business"
    })
@EnableFeignClients(basePackages = "com.backend.business.client")
public class BusinessServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(BusinessServiceApplication.class, args);
  }
}
