package com.codegenerate.aicodegenerate.ai;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class ImageCollectionServiceTest {

    @Resource
    ImageCollectionService imageCollectionService;


    @Test
    public void imageCollectionService(){
       String result =  imageCollectionService.collectImages("创建一个技术博客网站，极简风格，展示编程教程和相应的图片装饰");

        System.out.println("生成结果："+result);
    }

}