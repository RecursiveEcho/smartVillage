package com.backend.mediaservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@MapperScan("com.backend.media.mapper")
@SpringBootApplication(
    scanBasePackages = {"com.backend.common", "com.backend.media", "com.backend.mediaservice"})
@EnableFeignClients(basePackages = "com.backend.mediaservice.client")
public class MediaServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(MediaServiceApplication.class, args);
  }
}
