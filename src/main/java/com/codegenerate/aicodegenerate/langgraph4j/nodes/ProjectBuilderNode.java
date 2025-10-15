package com.codegenerate.aicodegenerate.langgraph4j.nodes;

import com.codegenerate.aicodegenerate.ai.VueProjectBuild;
import com.codegenerate.aicodegenerate.ai.model.aienum.CodeGenTypeEnum;
import com.codegenerate.aicodegenerate.exception.BussessException;
import com.codegenerate.aicodegenerate.exception.ErrorCode;
import com.codegenerate.aicodegenerate.langgraph4j.state.WorkflowContext;
import com.codegenerate.aicodegenerate.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.AsyncNodeAction;
import org.bsc.langgraph4j.prebuilt.MessagesState;

import java.io.File;

import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

@Slf4j
public class ProjectBuilderNode {

    public static AsyncNodeAction<MessagesState<String>> create() {
        return node_async(state -> {
            WorkflowContext context = WorkflowContext.getContext(state);
            log.info("执行节点: 项目构建");

            // 获取必要的参数
            String generatedCodeDir = context.getGeneratedCodeDir();
            CodeGenTypeEnum generationType = context.getGenerationType();
            String buildResultDir;
            // Vue 项目类型：使用 VueProjectBuilder 进行构建 可以用langgraph 条件边来执行
                try {
                    VueProjectBuild vueBuilder = SpringContextUtil.getBean(VueProjectBuild.class);
                    // 执行 Vue 项目构建（npm install + npm run build）
                    boolean buildSuccess = vueBuilder.buildProject(generatedCodeDir);
                    if (buildSuccess) {
                        // 构建成功，返回 dist 目录路径
                        buildResultDir = generatedCodeDir + File.separator + "dist";
                        log.info("Vue 项目构建成功，dist 目录: {}", buildResultDir);
                    } else {
                        throw new BussessException(ErrorCode.SYSTEM_ERROR.getCode(), "Vue 项目构建失败");
                    }
                } catch (Exception e) {
                    log.error("Vue 项目构建异常: {}", e.getMessage(), e);
                    buildResultDir = generatedCodeDir; // 异常时返回原路径
                }

            // 更新状态
            context.setCurrentStep("项目构建");
            context.setBuildResultDir(buildResultDir);
            log.info("项目构建节点完成，最终目录: {}", buildResultDir);
            return WorkflowContext.saveContext(context);
        });
    }
}

