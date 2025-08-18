package com.aetherbuilder.nocode;

import dev.langchain4j.community.store.embedding.redis.spring.RedisEmbeddingStoreAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@MapperScan("com.aetherbuilder.nocode.mapper")
@EnableCaching
public class AetherbuilderApplication {

    public static void main(String[] args) {
        SpringApplication.run(AetherbuilderApplication.class, args);
    }

}