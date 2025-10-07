package com.codegenerate.aicodegenerate;

import dev.langchain4j.community.store.embedding.redis.spring.RedisEmbeddingStoreAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.codegenerate.aicodegenerate.mapper")
@SpringBootApplication(exclude = {RedisEmbeddingStoreAutoConfiguration.class})
public class AiCodeGenerateApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiCodeGenerateApplication.class, args);
    }

}
