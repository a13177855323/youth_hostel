package com.youthhostel.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.youthhostel")
@MapperScan("com.youthhostel.mapper")
public class YouthHostelApplication {

    public static void main(String[] args) {
        SpringApplication.run(YouthHostelApplication.class, args);
    }
}
