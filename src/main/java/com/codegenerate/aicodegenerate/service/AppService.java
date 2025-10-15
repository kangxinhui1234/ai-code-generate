package com.codegenerate.aicodegenerate.service;

import com.codegenerate.aicodegenerate.entity.App;
import com.codegenerate.aicodegenerate.entity.User;
import com.codegenerate.aicodegenerate.model.dto.app.AppQueryRequest;
import com.codegenerate.aicodegenerate.model.vo.AppVO;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 应用 服务层。
 *
 * @author kxh
 */
public interface AppService extends IService<App> {
    public AppVO getAppVO(App app) ;

    List<AppVO> getAppVOList(List<App> records);

    QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest);

    Flux<String> chatToGenCode(Long appId, String message, User loginUser);

    String deployApp(Long appId, User loginUser);

    /**
     * 删除应用（需要同时删除关联的对话历史）
     *
     * @param appId 应用ID
     * @param loginUser 登录用户
     * @return 是否删除成功
     */
    boolean deleteApp(Long appId, User loginUser);


    /**
     * 应用聊天生成代码（流式）
     *
     * @param appId   应用 ID
     * @param message 用户消息
     * @param loginUser 登录用户
     * @param agent 是否启用 Agent 模式
     * @return 生成的代码流
     */
    Flux<String> chatToGenCode(Long appId, String message, User loginUser, boolean agent);

}