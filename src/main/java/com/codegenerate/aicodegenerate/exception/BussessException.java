package com.codegenerate.aicodegenerate.exception;

public class BussessException extends RuntimeException{

    private  final int code;
    public BussessException(int code,String message){
        super(message);
        this.code = code;
    }

    public BussessException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.code = errorCode.getCode();

    }


}
