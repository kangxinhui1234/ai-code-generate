package com.codegenerate.aicodegenerate.service.impl;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.codegenerate.aicodegenerate.configuration.OssProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OssService {

    private final OssProperties ossProperties;

    /**
     * 创建OSS客户端（使用接入点）
     */
    private OSS createOssClient() {
        log.info("OSS Endpoint: {}", ossProperties.getEndpoint());
        log.info("OSS Bucket: {}", ossProperties.getBucketName());
// 对于接入点，需要创建特殊配置
        ClientBuilderConfiguration config = new ClientBuilderConfiguration();

        return new OSSClientBuilder().build(
                ossProperties.getEndpoint(),
                ossProperties.getAccessKeyId(),
                ossProperties.getAccessKeySecret(),
                config
        );

    }

    /**
     * 上传本地文件到OSS
     */
    public String uploadLocalFile(File file, String path)  {
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("文件不存在");
        }
        // 验证文件大小
        long fileSize = file.length();
        if (fileSize > ossProperties.getMaxSize()) {
            throw new IllegalArgumentException("文件大小不能超过" + (ossProperties.getMaxSize() / 1024 / 1024) + "MB");
        }
        // 生成唯一文件名
        String fileName = generateFileName(file.getName(), path);
        OSS ossClient = createOssClient();
        try (InputStream inputStream = new FileInputStream(file)) {

            // 创建上传请求
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    ossProperties.getBucketName(),
                    fileName,
                    inputStream
            );

            // 设置元数据
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(fileSize);

            // 自动检测内容类型
            String contentType = Files.probeContentType(file.toPath());
            if (contentType != null) {
                metadata.setContentType(contentType);
            }

            putObjectRequest.setMetadata(metadata);

            // 执行上传
            PutObjectResult result = ossClient.putObject(putObjectRequest);
            log.info("本地文件上传成功, ETag: {}", result.getETag());

            // 返回文件URL
            return ossProperties.getBaseUrl() + fileName;

        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
          //  throw new RuntimeException(e);
        } catch (IOException e) {
            log.error(e.getMessage());
           // throw new RuntimeException(e);
        }

        return "";
    }


    /**
     * 生成唯一文件名
     */
    private String generateFileName(String originalFilename, String path) {
        // 确保路径以斜杠结尾
        if (!path.endsWith("/")) {
            path += "/";
        }

        // 获取文件扩展名
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 生成唯一文件名
        return path + UUID.randomUUID() + extension;
    }

}