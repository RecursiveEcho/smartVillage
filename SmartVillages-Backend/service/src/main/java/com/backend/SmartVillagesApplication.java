package com.backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.backend.*.mapper")
@SpringBootApplication
public class SmartVillagesApplication {

  public static void main(String[] args) {
    SpringApplication.run(SmartVillagesApplication.class, args);
  }
}
