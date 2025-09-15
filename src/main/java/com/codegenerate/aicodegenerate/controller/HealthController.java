package com.codegenerate.aicodegenerate.controller;

import com.codegenerate.aicodegenerate.response.BaseResponse;
import com.codegenerate.aicodegenerate.response.ResultUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping("/")
    public BaseResponse<String> healthCheck() {
        return  ResultUtil.sucess("ok");
    }
}
