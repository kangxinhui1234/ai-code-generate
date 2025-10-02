package com.codegenerate.aicodegenerate.exception;


import com.codegenerate.aicodegenerate.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;


@ControllerAdvice
@Component
@Slf4j
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(BussessException.class)
    @ResponseBody // 添加这个注解
    public BaseResponse handlerBusException(BussessException e){
         BaseResponse response = new BaseResponse(e.getCode(),e.getMessage(),null);
         log.error(response.toString());
         return response;
      //  return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);

    }


    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    @ResponseBody // 添加这个注解
    public BaseResponse handlerBusException(Exception e){
        BaseResponse response = new BaseResponse(ErrorCode.SYSTEM_ERROR.getCode(),e.getMessage(),null);
        log.error(response.toString());
        return response;
        //  return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);

    }
}
