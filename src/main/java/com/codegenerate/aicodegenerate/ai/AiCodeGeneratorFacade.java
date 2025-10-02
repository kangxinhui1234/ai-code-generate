package com.codegenerate.aicodegenerate.ai;

import com.codegenerate.aicodegenerate.ai.model.AiHtmlResult;
import com.codegenerate.aicodegenerate.ai.model.MultiFileCodeResult;
import com.codegenerate.aicodegenerate.ai.model.aienum.CodeGenTypeEnum;
import com.codegenerate.aicodegenerate.exception.BussessException;
import com.codegenerate.aicodegenerate.exception.ErrorCode;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;

/**
 * AI 代码生成外观类，组合生成和保存功能
 */
@Service
@Slf4j
public class AiCodeGeneratorFacade {

    @Resource
    private AiCodeGeneratorService aiCodeGeneratorService;

    /**
     * 统一入口：根据类型生成并保存代码
     *
     * @param userMessage     用户提示词
     * @param codeGenTypeEnum 生成类型
     * @return 保存的目录
     */
    public Flux<String> generateAndSaveCode(String userMessage, CodeGenTypeEnum codeGenTypeEnum) {
        if (codeGenTypeEnum == null) {
            throw new BussessException(ErrorCode.SYSTEM_ERROR.getCode(), "生成类型为空");
        }
        return switch (codeGenTypeEnum) {
            case HTML_CODE -> generateAndSaveHtmlCodeStream(userMessage);
            case HTML_MULTI_CODE -> generateAndSaveMultiFileCodeStream(userMessage);
            default -> {
                String errorMessage = "不支持的生成类型：" + codeGenTypeEnum.getType();
                throw new BussessException(ErrorCode.SYSTEM_ERROR.getCode(), errorMessage);
            }
        };
    }

    /**
     * 生成 HTML 模式的代码并保存
     *
     * @param userMessage 用户提示词
     * @return 保存的目录
     */
    private Flux<String> generateAndSaveHtmlCodeStream(String userMessage) {
        Flux<String> result = aiCodeGeneratorService.generateCode(userMessage);
        StringBuilder builder = new StringBuilder();
        return result.doOnNext((data)->{
            builder.append(data);
            System.out.println(data);
        })
           .doOnComplete(()->{
                  //  CodeParser.parseHtmlCode(builder.toString());
               CodeFileSave.saveSingleHtml(CodeParser.parseHtmlCode(builder.toString()));
               log.info("写入文件成功");
                });

    }

    /**
     * 生成多文件模式的代码并保存
     *
     * @param userMessage 用户提示词
     * @return 保存的目录
     */
    private Flux<String> generateAndSaveMultiFileCodeStream(String userMessage) {
        Flux<String> result = aiCodeGeneratorService.generateCodeMulti(userMessage);
        StringBuilder builder = new StringBuilder();
        return result.doOnNext((data)->{
                    builder.append(data);
                })
                .doOnComplete(()->{
                    //  CodeParser.parseHtmlCode(builder.toString());
                    CodeFileSave.saveSingleHtml(CodeParser.parseHtmlCode(builder.toString()));
                    log.info("写入文件成功");

                });
    }
}
