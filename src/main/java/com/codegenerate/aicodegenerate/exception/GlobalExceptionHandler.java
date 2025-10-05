package com.codegenerate.aicodegenerate.exception;


import com.codegenerate.aicodegenerate.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


@ControllerAdvice
@Component
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BussessException.class)
    @ResponseBody // 添加这个注解
    public ResponseEntity<BaseResponse> handlerBusException(BussessException e){
         BaseResponse response = new BaseResponse(e.getCode(),e.getMessage(),null);
         log.error(response.toString());
      //   return response;
       return new ResponseEntity<>(response, HttpStatus.OK);

    }


    @ExceptionHandler(Exception.class)
    @ResponseBody // 添加这个注解
    public ResponseEntity<BaseResponse> handlerBusException2(Exception e){
        BaseResponse response = new BaseResponse(ErrorCode.SYSTEM_ERROR.getCode(),e.getMessage(),null);
        log.error(response.toString());
       // return response;
          return new ResponseEntity<>(response, HttpStatus.OK);

    }
}
