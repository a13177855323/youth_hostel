package com.youthhostel;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("com.youthhostel.mapper")
@EnableTransactionManagement
public class YouthHostelAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(YouthHostelAdminApplication.class, args);
    }
}
