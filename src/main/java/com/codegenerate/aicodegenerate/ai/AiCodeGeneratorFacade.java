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



    /**
     * 统一入口：根据类型生成并保存代码（使用 appId）
     *
     * @param userMessage     用户提示词
     * @param codeGenTypeEnum 生成类型
     * @return 保存的目录
     */
//    public File generateAndSaveCode(String userMessage, CodeGenTypeEnum codeGenTypeEnum, Long appId) {
//        if (codeGenTypeEnum == null) {
//            throw new BussessException(ErrorCode.SYSTEM_ERROR.getCode(), "生成类型为空");
//        }
//        return switch (codeGenTypeEnum) {
//            case HTML_CODE -> {
//                AiHtmlResult result = aiCodeGeneratorService.generateCode(userMessage);
//                yield CodeFileSaverExecutor.executeSaver(result, CodeGenTypeEnum.HTML, appId);
//            }
//            case HTML_MULTI_CODE -> {
//                MultiFileCodeResult result = aiCodeGeneratorService.generateMultiFileCode(userMessage);
//                yield CodeFileSaverExecutor.executeSaver(result, CodeGenTypeEnum.MULTI_FILE, appId);
//            }
//            default -> {
//                String errorMessage = "不支持的生成类型：" + codeGenTypeEnum.getValue();
//                throw new BusinessException(ErrorCode.SYSTEM_ERROR, errorMessage);
//            }
//        };
//    }

    /**
     * 统一入口：根据类型生成并保存代码（流式，使用 appId）
     *
     * @param userMessage     用户提示词
     * @param codeGenTypeEnum 生成类型
     * @param appId           应用 ID
     */
    public Flux<String> generateAndSaveCodeStream(String userMessage, CodeGenTypeEnum codeGenTypeEnum, Long appId) {
        if (codeGenTypeEnum == null) {
            throw new BussessException(ErrorCode.SYSTEM_ERROR.getCode(), "生成类型为空");
        }
        return switch (codeGenTypeEnum) {
            case HTML_CODE -> {
                Flux<String> codeStream = aiCodeGeneratorService.generateCode(userMessage);
                yield processCodeStream(codeStream, CodeGenTypeEnum.HTML_CODE, appId);
            }
            case HTML_MULTI_CODE -> {
                Flux<String> codeStream = aiCodeGeneratorService.generateCodeMulti(userMessage);
                yield processCodeStream(codeStream, CodeGenTypeEnum.HTML_MULTI_CODE, appId);
            }
            default -> {
                String errorMessage = "不支持的生成类型：" + codeGenTypeEnum.getType();
                throw new BussessException(ErrorCode.SYSTEM_ERROR.getCode(), errorMessage);
            }
        };
    }

    /**
     * 通用流式代码处理方法（使用 appId）
     *
     * @param codeStream  代码流
     * @param codeGenType 代码生成类型
     * @param appId       应用 ID
     * @return 流式响应
     */
    private Flux<String> processCodeStream(Flux<String> codeStream, CodeGenTypeEnum codeGenType, Long appId) {
        StringBuilder codeBuilder = new StringBuilder();
        return codeStream.doOnNext(chunk -> {
            // 实时收集代码片段
            codeBuilder.append(chunk);
        }).doOnComplete(() -> {
            // 流式返回完成后保存代码
            try {
                String completeCode = codeBuilder.toString();
                String savedDir = "";
                if(CodeGenTypeEnum.HTML_CODE.equals(codeGenType)){
                    // 使用执行器解析代码
                    AiHtmlResult parsedResult = CodeParser.parseHtmlCode(completeCode);
                     savedDir = CodeFileSave.saveSingleHtml(parsedResult, appId);
                }else{
                    // 使用执行器解析代码
                    MultiFileCodeResult parsedResult = CodeParser.parseMultiFileCode(completeCode);
                     savedDir = CodeFileSave.saveMultiHtml(parsedResult, appId);
                }

                log.info("保存成功，路径为：" + savedDir);
            } catch (Exception e) {
                log.error("保存失败: {}", e.getMessage());
            }
        });
    }

}
