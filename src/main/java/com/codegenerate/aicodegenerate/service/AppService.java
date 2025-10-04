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
}
