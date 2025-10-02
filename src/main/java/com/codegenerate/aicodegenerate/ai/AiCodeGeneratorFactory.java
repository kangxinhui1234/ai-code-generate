package com.codegenerate.aicodegenerate.ai;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.ControllerAdvice;

@Configuration
public class AiCodeGeneratorFactory {

    @Resource
    ChatModel chatModel;
    @Resource
    StreamingChatModel streamingChatModel;

    @Bean
    public AiCodeGeneratorService aiCodeGeneratorService(){

        return AiServices.builder(AiCodeGeneratorService.class)
                .chatModel(chatModel)
                .streamingChatModel(streamingChatModel)
                .build();
    }

}
