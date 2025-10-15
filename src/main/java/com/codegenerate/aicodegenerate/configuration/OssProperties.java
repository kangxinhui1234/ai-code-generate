package com.codegenerate.aicodegenerate.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "alyun-oss")
public class OssProperties {
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    // 不再需要 bucketName 属性
    // private String bucketName;
    private long maxSize;
    private String callbackUrl;
    private long expireTime;
    private String baseUrl;
    private String bucketName;

}