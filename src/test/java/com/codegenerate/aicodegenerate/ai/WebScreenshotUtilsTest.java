package com.codegenerate.aicodegenerate.ai;

import com.codegenerate.aicodegenerate.service.impl.OssService;
import com.codegenerate.aicodegenerate.utils.WebScreenshotUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;

@Slf4j
@SpringBootTest
public class WebScreenshotUtilsTest {

    @Autowired
    OssService ossService;

    @Test
    void saveWebPageScreenshot() {
        String testUrl = "https://www.codefather.cn";
        String webPageScreenshot = WebScreenshotUtils.saveWebPageScreenshot(testUrl);
        Assertions.assertNotNull(webPageScreenshot);
    }


    @Test
    void uploadFIle() throws IOException {
       String url =  ossService.uploadLocalFile(new File("C:\\Users\\kxh\\Desktop\\bccb0431c2e24043867e304245beef5d.png"),
                "screenshot/");
       System.out.println("下载地址："+url);
    }
}
