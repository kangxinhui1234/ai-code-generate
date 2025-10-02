package com.codegenerate.aicodegenerate.ai;

import com.codegenerate.aicodegenerate.ai.model.AiHtmlResult;
import com.codegenerate.aicodegenerate.ai.model.aienum.CodeGenTypeEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AiCodeGeneratorFactoryTest {

    @Autowired
    AiCodeGeneratorFactory aiCodeGeneratorFactory;


    @Autowired
    CodeFileSave codeFileSave;
    @Autowired
    AiCodeGeneratorService aiCodeGeneratorService;

    @Autowired
    AiCodeGeneratorFacade aiCodeGeneratorFacade;


    @Test
    public void setAiCodeGenerator(){
        Flux<String> html =    aiCodeGeneratorService.generateCode("帮我生成一个简历的html");
       List<String> ls =  html.collectList().block();
       String totalHtml = String.join("",ls);
      //  codeFileSave.saveSingleHtml(html);
      //  System.out.println(html);

    }
    @Test
    public void generateAndSaveCode(){
        Flux<String> html =    aiCodeGeneratorFacade.generateAndSaveCode("帮我生成一个简历的html", CodeGenTypeEnum.HTML_CODE);
        List<String> ls =  html.collectList().block();
        String totalHtml = String.join("",ls);
        //  codeFileSave.saveSingleHtml(html);
        //  System.out.println(html);

    }

}