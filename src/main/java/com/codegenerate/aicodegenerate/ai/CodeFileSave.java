package com.codegenerate.aicodegenerate.ai;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.codegenerate.aicodegenerate.ai.model.AiHtmlResult;
import com.codegenerate.aicodegenerate.ai.model.MultiFileCodeResult;
import com.codegenerate.aicodegenerate.ai.model.aienum.CodeGenTypeEnum;
import org.springframework.stereotype.Component;

/**
 * 文件存储在本地
 * 接受ai大模型返回的html结果
 */
@Component
public class CodeFileSave {

    private static  String ROOT_DIR = System.getProperty("user.dir") +"/tmp/";

    public static String saveSingleHtml(AiHtmlResult result){
        // 1、创建文件存储路径
        // 2、文件名生成 要用唯一id
        String fileName = CodeGenTypeEnum.HTML_CODE.getType() + "_" + IdUtil.getSnowflakeNextIdStr()+".html";
        String filePath = ROOT_DIR + fileName;
        FileUtil.writeUtf8String(result.getHtmlCode(),filePath);
        return filePath;

    }



    /**
     * 多文件生成
     * @param result
     * @return
     */
    public static String saveMultiHtml(MultiFileCodeResult result){
        // 1、创建文件存储路径
        // 2、文件夹生成 要用唯一id
        String fileDirName = CodeGenTypeEnum.HTML_MULTI_CODE.getType() + "_" + IdUtil.getSnowflakeNextIdStr()+"/";
        String filePath = ROOT_DIR + fileDirName;
        FileUtil.writeUtf8String(result.getHtmlCode(),filePath+"index.html");
        FileUtil.writeUtf8String(result.getCssCode(),filePath+"style.css");
        FileUtil.writeUtf8String(result.getJsCode(),filePath+"script.js");
        return filePath;

    }

}
