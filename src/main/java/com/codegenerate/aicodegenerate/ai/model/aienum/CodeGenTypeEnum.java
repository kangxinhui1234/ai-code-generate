package com.codegenerate.aicodegenerate.ai.model.aienum;

/**
 * 代码生成的类型
 */
public enum CodeGenTypeEnum {

    HTML_CODE("SINGLE_HTML_CODE","单文件生成"),
    HTML_MULTI_CODE("HTML_MULTI_CODE","多文件生成");

    String type;
    String msg;

     CodeGenTypeEnum(String type, String msg) {
        this.type = type;
        this.msg = msg;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
