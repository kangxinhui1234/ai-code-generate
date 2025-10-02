package com.codegenerate.aicodegenerate.service.impl;

import com.codegenerate.aicodegenerate.entity.App;
import com.codegenerate.aicodegenerate.mapper.AppMapper;
import com.codegenerate.aicodegenerate.service.AppService;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 应用 服务层实现。
 *
 * @author kxh
 */
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App> implements AppService {

    @Override
    public AppMapper getMapper() {
        return null;
    }
}
