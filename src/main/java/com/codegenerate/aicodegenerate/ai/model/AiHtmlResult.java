package com.codegenerate.aicodegenerate.ai.model;


import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

@Data
@Description("html代码生成结果")  // 添加描述 可以有助于大模型结构化输出
public class AiHtmlResult {
        @Description("html代码")
        private String htmlCode;

        private String description;


}
