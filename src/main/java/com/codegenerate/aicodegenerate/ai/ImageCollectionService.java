package com.codegenerate.aicodegenerate.ai;

import com.codegenerate.aicodegenerate.langgraph4j.state.ImageResource;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

import java.util.List;

/**
 * 图片收集 AI 服务接口
 * 使用 AI 调用工具收集不同类型的图片资源
 */
public interface ImageCollectionService {

    /**
     * 根据用户提示词收集所需的图片资源
     * AI 会根据需求自主选择调用相应的工具
     * 原本结构化输出 但是大模型兼容情况不一  所以直接使用返回字符串
     */
    @SystemMessage(fromResource = "prompt/image-collection-system-prompt.txt")
    String collectImages(@UserMessage String userPrompt);
}
