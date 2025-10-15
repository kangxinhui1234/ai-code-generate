package com.codegenerate.aicodegenerate.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.codegenerate.aicodegenerate.ai.AiCodeGeneratorFacade;
import com.codegenerate.aicodegenerate.ai.VueProjectBuild;
import com.codegenerate.aicodegenerate.ai.model.aienum.CodeGenTypeEnum;
import com.codegenerate.aicodegenerate.ai.model.handler.StreamHandlerExecutor;
import com.codegenerate.aicodegenerate.constants.AppConstant;
import com.codegenerate.aicodegenerate.constants.UserConstant;
import com.codegenerate.aicodegenerate.entity.App;
import com.codegenerate.aicodegenerate.entity.User;
import com.codegenerate.aicodegenerate.exception.BussessException;
import com.codegenerate.aicodegenerate.exception.ErrorCode;
import com.codegenerate.aicodegenerate.langgraph4j.CodeGenWorkflow;
import com.codegenerate.aicodegenerate.mapper.AppMapper;
import com.codegenerate.aicodegenerate.model.dto.app.AppQueryRequest;
import com.codegenerate.aicodegenerate.model.vo.AppVO;
import com.codegenerate.aicodegenerate.model.vo.UserVO;
import com.codegenerate.aicodegenerate.monitor.MonitorContext;
import com.codegenerate.aicodegenerate.monitor.MonitorContextHolder;
import com.codegenerate.aicodegenerate.service.AppService;
import com.codegenerate.aicodegenerate.service.ChatHistoryService;
import com.codegenerate.aicodegenerate.service.ScreenshotService;
import com.codegenerate.aicodegenerate.service.UserService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

/**
 * 应用 服务层实现。
 *
 * @author kxh
 */
@Service
@Slf4j
public class AppServiceImpl extends ServiceImpl<AppMapper, App> implements AppService {

    @Resource
    private ScreenshotService screenshotService;
    @Autowired
    UserService userService;

    @Autowired
    AiCodeGeneratorFacade aiCodeGeneratorFacade;

    @Autowired
    ChatHistoryService chatHistoryService;

    @Autowired
    StreamHandlerExecutor streamHandlerExecutor;

    @Autowired
    VueProjectBuild vueProjectBuild;

    public QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest) {
        if (appQueryRequest == null) {
            throw new BussessException(ErrorCode.PARAMS_ERROR.getCode(), "请求参数为空");
        }
        Long id = appQueryRequest.getId();
        String appName = appQueryRequest.getAppName();
        String cover = appQueryRequest.getCover();
        String initPrompt = appQueryRequest.getInitPrompt();
        String codeGenType = appQueryRequest.getCodeGenType();
        String deployKey = appQueryRequest.getDeployKey();
        Integer priority = appQueryRequest.getPriority();
        Long userId = appQueryRequest.getUserId();
        String sortField = appQueryRequest.getSortField();
        String sortOrder = appQueryRequest.getSortOrder();
        return QueryWrapper.create()
                .eq("id", id)
                .like("appName", appName)
                .like("cover", cover)
                .like("initPrompt", initPrompt)
                .eq("codeGenType", codeGenType)
                .eq("deployKey", deployKey)
                .eq("priority", priority)
                .eq("userId", userId)
                .orderBy(sortField, "ascend".equals(sortOrder));
    }

    @Override
    public AppVO getAppVO(App app) {
        if (app == null) {
            return null;
        }
        AppVO appVO = new AppVO();
        BeanUtil.copyProperties(app, appVO);
        // 关联查询用户信息
        Long userId = app.getUserId();
        if (userId != null) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            appVO.setUser(userVO);
        }
        return appVO;
    }



    @Override
    public List<AppVO> getAppVOList(List<App> appList) {
        if (CollUtil.isEmpty(appList)) {
            return new ArrayList<>();
        }
        // 批量获取用户信息，避免 N+1 查询问题
        Set<Long> userIds = appList.stream()
                .map(App::getUserId)
                .collect(Collectors.toSet());
        Map<Long, UserVO> userVOMap = userService.listByIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, userService::getUserVO));
        return appList.stream().map(app -> {
            AppVO appVO = getAppVO(app);
            UserVO userVO = userVOMap.get(app.getUserId());
            appVO.setUser(userVO);
            return appVO;
        }).collect(Collectors.toList());
    }



    public Flux<String> chatToGenCode(Long appId, String message, User loginUser) {
        // 1. 参数校验
       // ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用 ID 不能为空");
      //  ThrowUtils.throwIf(StrUtil.isBlank(message), ErrorCode.PARAMS_ERROR, "用户消息不能为空");
        // 2. 查询应用信息
        App app = this.getById(appId);
     //   ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        // 3. 验证用户是否有权限访问该应用，仅本人可以生成代码
        if (!app.getUserId().equals(loginUser.getId())) {
            throw new BussessException(ErrorCode.NO_AUTH_ERROR.getCode(), "无权限访问该应用");
        }
        
        // 4. 保存用户消息到对话历史
        try {
            chatHistoryService.saveUserMessage(appId, loginUser.getId(), message);
        } catch (Exception e) {
            log.error("保存用户消息失败: {}", e.getMessage());
            // 保存失败不影响主流程，继续执行
        }

        // 6. 设置监控上下文
        MonitorContextHolder.setContext(
                MonitorContext.builder()
                        .userId(loginUser.getId().toString())
                        .appId(appId.toString())
                        .build()
        );



        // 暂时设置为 VUE 工程生成
       // app.setCodeGenType(CodeGenTypeEnum.VUE_PROJECT.getType());

        // 5. 获取应用的代码生成类型
        String codeGenTypeStr = app.getCodeGenType();
        CodeGenTypeEnum codeGenTypeEnum = CodeGenTypeEnum.getEnumByValue(codeGenTypeStr);
        if (codeGenTypeEnum == null) {
            throw new BussessException(ErrorCode.SYSTEM_ERROR.getCode(), "不支持的代码生成类型");
        }
        
        // 6. 调用 AI 生成代码（流式）
        StringBuilder aiResponseBuilder = new StringBuilder();
        
        Flux<String> aiResponse =  aiCodeGeneratorFacade.generateAndSaveCodeStream(message, codeGenTypeEnum, appId);
       return  streamHandlerExecutor.doExecute(aiResponse,chatHistoryService,appId,loginUser,codeGenTypeEnum)
               .doFinally(
               signalType -> {
                   // 流结束时清理（无论成功/失败/取消）
                   MonitorContextHolder.clearContext();
               });

//                .doOnNext(aiResponse -> {
//                    // 7. 收集AI回复片段
//                    aiResponseBuilder.append(aiResponse);
//                })
//                .doOnComplete(() -> {
//                    // 8. AI回复完成，保存完整的回复到对话历史
//                    try {
//                        String completeAiResponse = aiResponseBuilder.toString();
//                        if (!completeAiResponse.trim().isEmpty()) {
//                            chatHistoryService.saveAiMessage(appId, loginUser.getId(), completeAiResponse);
//                            log.info("成功保存AI完整回复，长度: {}", completeAiResponse.length());
//                        }
//                    } catch (Exception e) {
//                        log.error("保存AI完整回复失败: {}", e.getMessage());
//                    }
//                })
//                .doOnError(error -> {
//                    // 9. 保存错误信息到对话历史
//                    try {
//                        String errorMessage = "AI生成代码失败: " + error.getMessage();
//                        chatHistoryService.saveErrorMessage(appId, loginUser.getId(), errorMessage);
//                        log.error("AI生成代码失败: {}", error.getMessage());
//                    } catch (Exception e) {
//                        log.error("保存错误信息失败: {}", e.getMessage());
//                    }
//                });
    }


    @Override
    public String deployApp(Long appId, User loginUser) {
        // 1. 参数校验
      //  ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用 ID 不能为空");
      //  ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        // 2. 查询应用信息
        App app = this.getById(appId);
      //  ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        // 3. 验证用户是否有权限部署该应用，仅本人可以部署
        if (!app.getUserId().equals(loginUser.getId())) {
            throw new BussessException(ErrorCode.NO_AUTH_ERROR.getCode(), "无权限部署该应用");
        }
        // 4. 检查是否已有 deployKey
        String deployKey = app.getDeployKey();
        // 没有则生成 6 位 deployKey（大小写字母 + 数字）
        if (StrUtil.isBlank(deployKey)) {
            deployKey = RandomUtil.randomString(6);
        }
        // 5. 获取代码生成类型，构建源目录路径
        String codeGenType = app.getCodeGenType();
        String sourceDirName = codeGenType + "_" + appId;
        String sourceDirPath = AppConstant.CODE_OUTPUT_ROOT_DIR + File.separator + sourceDirName;
        // 6. 检查源目录是否存在
        File sourceDir = new File(sourceDirPath);
        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
            throw new BussessException(ErrorCode.SYSTEM_ERROR.getCode(), "应用代码不存在，请先生成代码");
        }

        // 7. Vue 项目特殊处理：执行构建
        CodeGenTypeEnum codeGenTypeEnum = CodeGenTypeEnum.getEnumByValue(codeGenType);
        if (codeGenTypeEnum == CodeGenTypeEnum.VUE_PROJECT) {
            sourceDirPath = AppConstant.CODE_OUTPUT_ROOT_DIR + File.separator + "vue_projects" + "vue_"+appId;
            // Vue 项目需要构建
            boolean buildSuccess = vueProjectBuild.buildProject(sourceDirPath);
          //  ThrowUtils.throwIf(!buildSuccess, ErrorCode.SYSTEM_ERROR, "Vue 项目构建失败，请检查代码和依赖");
            // 检查 dist 目录是否存在
            File distDir = new File(sourceDirPath, "dist");
          //  ThrowUtils.throwIf(!distDir.exists(), ErrorCode.SYSTEM_ERROR, "Vue 项目构建完成但未生成 dist 目录");
            // 将 dist 目录作为部署源
            sourceDir = distDir;
            log.info("Vue 项目构建成功，将部署 dist 目录: {}", distDir.getAbsolutePath());
        }


        // 7. 复制文件到部署目录
        String deployDirPath = AppConstant.CODE_DEPLOY_ROOT_DIR + File.separator + deployKey;
        try {
            FileUtil.copyContent(sourceDir, new File(deployDirPath), true);
        } catch (Exception e) {
            throw new BussessException(ErrorCode.SYSTEM_ERROR.getCode(), "部署失败：" + e.getMessage());
        }
        // 8. 更新应用的 deployKey 和部署时间
        App updateApp = new App();
        updateApp.setId(appId);
        updateApp.setDeployKey(deployKey);
        updateApp.setDeployedTime(LocalDateTime.now());
        boolean updateResult = this.updateById(updateApp);
        // ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR, "更新应用部署信息失败");
        // 9. 返回可访问的 URL
        String deployUrl = String.format("%s/%s/", AppConstant.CODE_DEPLOY_HOST, deployKey);
        generateAppScreenshotAsync(appId,deployUrl); // 异步创建截图
        return deployUrl;
    }


    /**
     * 异步生成应用截图并更新封面
     *
     * @param appId  应用ID
     * @param appUrl 应用访问URL
     */
    public void generateAppScreenshotAsync(Long appId, String appUrl) {
        // 使用虚拟线程异步执行
        Thread.startVirtualThread(() -> {
            // 调用截图服务生成截图并上传
            String screenshotUrl = screenshotService.generateAndUploadScreenshot(appUrl);
            // 更新应用封面字段
            App updateApp = new App();
            updateApp.setId(appId);
            updateApp.setCover(screenshotUrl);
            boolean updated = this.updateById(updateApp);
          //  ThrowUtils.throwIf(!updated, ErrorCode.OPERATION_ERROR, "更新应用封面字段失败");
        });
    }



    @Override
    public boolean deleteApp(Long appId, User loginUser) {
        // 1. 参数校验
        if (appId == null || appId <= 0) {
            throw new BussessException(ErrorCode.PARAMS_ERROR.getCode(), "应用ID不能为空");
        }
        if (loginUser == null) {
            throw new BussessException(ErrorCode.NOT_LOGIN_ERROR.getCode(), "用户未登录");
        }
        
        // 2. 查询应用信息
        App app = this.getById(appId);
        if (app == null) {
            throw new BussessException(ErrorCode.NOT_FOUND_ERROR.getCode(), "应用不存在");
        }
        
        // 3. 验证用户是否有权限删除该应用，仅本人或管理员可以删除
        if (!app.getUserId().equals(loginUser.getId()) && !UserConstant.ADMIN_ROLE.equals(loginUser.getUserRole())) {
            throw new BussessException(ErrorCode.NO_AUTH_ERROR.getCode(), "无权限删除该应用");
        }
        
        // 4. 删除关联的对话历史
        try {
            chatHistoryService.deleteByAppId(appId);
            log.info("成功删除应用 {} 的对话历史", appId);
        } catch (Exception e) {
            log.error("删除应用 {} 的对话历史失败: {}", appId, e.getMessage());
            // 对话历史删除失败不影响应用删除，继续执行
        }
        
        // 5. 删除应用
        boolean result = this.removeById(appId);
        if (result) {
            log.info("成功删除应用: {}", appId);
        } else {
            log.error("删除应用失败: {}", appId);
        }
        
        return result;
    }

    /**
     * 判断走工作流还是走原始流式输出
     * @param appId   应用 ID
     * @param message 用户消息
     * @param loginUser 登录用户
     * @param agent 是否启用 Agent 模式 true-工作流
     * @return
     */
    @Override
    public Flux<String> chatToGenCode(Long appId, String message, User loginUser, boolean agent) {
        // 6. 根据 agent 参数选择生成方式
        Flux<String> codeStream;
        if (agent) {
            // Agent 模式：使用工作流生成代码
            codeStream = new CodeGenWorkflow().executeWorkflowWithFlux(message, appId);
        } else {
            // 传统模式：调用 AI 生成代码（流式）
            codeStream = this.chatToGenCode(appId, message, loginUser);
        }
        return codeStream;

    }


}
