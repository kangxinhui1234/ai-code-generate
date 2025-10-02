package com.codegenerate.aicodegenerate.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@JsonComponent  // Spring Boot 专用注解，自动注册 Jackson 组件
public class JsonConfig {

    @Bean
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        // 1. 创建默认 ObjectMapper（禁用 XML 映射）
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();

        // 2. 创建自定义序列化模块
        SimpleModule module = new SimpleModule();

        // 3. 添加 Long 类型序列化规则
        module.addSerializer(Long.class, ToStringSerializer.instance);      // 包装类 Long
        module.addSerializer(Long.TYPE, ToStringSerializer.instance);      // 基本类型 long

        // 4. 注册模块到 ObjectMapper
        objectMapper.registerModule(module);

        return objectMapper;
    }
}