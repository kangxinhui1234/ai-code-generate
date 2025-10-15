package com.codegenerate.aicodegenerate.ai.tools;

import cn.hutool.json.JSONObject;

public abstract class BaseTool {

    /**
     * 工具唯一标识
     */
    public abstract String getToolName();

    /**
     * 工具展示名称
     */
    public abstract String getDisplayName();

    /**
     * 执行工具逻辑
     */
    public abstract String generateToolExecutedResult(JSONObject arguments);
}
