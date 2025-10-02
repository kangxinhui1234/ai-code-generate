package com.codegenerate.aicodegenerate.ai;

import com.codegenerate.aicodegenerate.ai.model.AiHtmlResult;
import com.codegenerate.aicodegenerate.ai.model.MultiFileCodeResult;
import dev.langchain4j.service.SystemMessage;
import reactor.core.publisher.Flux;

/**
 * 声明式的客户端开发
 */
public interface AiCodeGeneratorService {

    /**
     * 生成单文件
     * @param userMessage
     * @return
     * 结构化输出速度太慢  流式输出不支持结构化
     */
//    @SystemMessage(fromResource = "prompt/codegen-single-html-syatem-prompt.txt")
//    AiHtmlResult generateCode(String userMessage);
    @SystemMessage(fromResource = "prompt/codegen-single-html-syatem-prompt.txt")
    Flux<String> generateCode(String userMessage);

    /**
     * 生成多文件
     * @param userMessage
     * @return
     */
    @SystemMessage(fromResource = "prompt/codegen-multi-html-system-prompt.txt")
    Flux<String>  generateCodeMulti(String userMessage);
}
