package com.codegenerate.aicodegenerate.ai;

import com.codegenerate.aicodegenerate.ai.model.AiHtmlResult;
import com.codegenerate.aicodegenerate.ai.model.MultiFileCodeResult;
import com.codegenerate.aicodegenerate.ai.model.aienum.CodeGenTypeEnum;
import com.codegenerate.aicodegenerate.guardrail.PromptSafetyInputGuardrail;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.guardrail.InputGuardrails;
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



    /**
     * 生成 Vue 项目代码（流式）
     *
     * @param userMessage 用户消息
     * @return 生成过程的流式响应
     */
    @SystemMessage(fromResource = "prompt/codegen-vue-system-prompt.txt")
    @InputGuardrails(PromptSafetyInputGuardrail.class) // 引入输入护轨
    TokenStream generateVueProjectCodeStream(@MemoryId long appId, @UserMessage String userMessage);

    Flux<String> generateCodeVueProject(String userMessage);





}
