package com.aetherbuilder.nocode;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.aetherbuilder.nocode.mapper")
public class AetherbuilderApplication {

    public static void main(String[] args) {
        SpringApplication.run(AetherbuilderApplication.class, args);
    }

}