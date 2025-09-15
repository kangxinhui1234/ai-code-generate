package com.codegenerate.aicodegenerate.response;

import com.codegenerate.aicodegenerate.exception.ErrorCode;

public class ResultUtil {

    public static <T> BaseResponse<T> sucess(T data){
        return new BaseResponse(0,"成功",data);
    }

    public static <T> BaseResponse sucess(){
        return new BaseResponse(0,"成功",null);
    }

    public static <T> BaseResponse error(ErrorCode errorCode){
        return new BaseResponse(errorCode.getCode(),errorCode.getMessage(),null);
    }
}
