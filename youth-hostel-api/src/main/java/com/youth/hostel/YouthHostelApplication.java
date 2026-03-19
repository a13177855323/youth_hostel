package com.youth.hostel;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.youth.hostel.dao.mapper")
public class YouthHostelApplication {

    public static void main(String[] args) {
        SpringApplication.run(YouthHostelApplication.class, args);
    }
}
