package com.backend.mediaservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan({"com.backend.media.mapper", "com.backend.auth.mapper"})
@SpringBootApplication(
    scanBasePackages = {"com.backend.common", "com.backend.media", "com.backend.mediaservice"})
public class MediaServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(MediaServiceApplication.class, args);
  }
}
