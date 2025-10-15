package com.codegenerate.aicodegenerate.ai;

import com.codegenerate.aicodegenerate.ai.model.aienum.CodeGenTypeEnum;
import dev.langchain4j.service.SystemMessage;

public interface AiCodeGenTypeRoutingService {


    /**
     * 根据用户需求智能选择代码生成类型
     *
     * @param userPrompt 用户输入的需求描述
     * @return 推荐的代码生成类型
     */
    @SystemMessage(fromResource = "prompt/codegen-route-system-prompt.txt")
    CodeGenTypeEnum routeCodeGenType(String userPrompt);
}
