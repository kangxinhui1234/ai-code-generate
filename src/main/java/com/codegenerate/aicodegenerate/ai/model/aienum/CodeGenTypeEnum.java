package com.codegenerate.aicodegenerate.ai.model.aienum;

import cn.hutool.core.util.ObjUtil;

/**
 * 代码生成的类型
 */
public enum CodeGenTypeEnum {

    HTML_CODE("SINGLE_HTML_CODE","单文件生成"),
    HTML_MULTI_CODE("HTML_MULTI_CODE","多文件生成"),
    VUE_PROJECT("Vue 工程模式", "vue_project");

    String type;
    String msg;

    /**
     * 根据 value 获取枚举
     *
     * @param value 枚举值的value
     * @return 枚举值
     */
    public static CodeGenTypeEnum getEnumByValue(String value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (CodeGenTypeEnum anEnum : CodeGenTypeEnum.values()) {
            if (anEnum.type.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

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
