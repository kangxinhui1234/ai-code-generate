package com.codegenerate.aicodegenerate.model.enums;

import lombok.Getter;
import cn.hutool.core.util.ObjUtil;
/**
 * 消息类型枚举
 *
 * @author kxh
 */
@Getter
public enum MessageTypeEnum {

    USER("用户消息", "user"),
    AI("AI回复", "ai"),
    ERROR("错误信息", "error");



    private final String text;

    private final String value;

    MessageTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value 枚举值的value
     * @return 枚举值
     */
    public static MessageTypeEnum getEnumByValue(String value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (MessageTypeEnum anEnum : MessageTypeEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }
}
