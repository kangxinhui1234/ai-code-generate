package com.codegenerate.aicodegenerate.ai.tools;

import com.codegenerate.aicodegenerate.constants.AppConstant;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;


/**
 * 写入文件工具
 * 写入文件位置： /tmp目录下
 *
 */
@Slf4j
public class FileWriteTools {

    @Tool
    public String writeFile(@P("要写入文件的相对路径") String relativeFilePath,
                            @P("要写入的文件内容") String content,
                            @ToolMemoryId Long appId) {
        try {
      // 写入文件
        // 规范路径格式（处理路径中的../和./）
        Path normalizedPath = Paths.get(relativeFilePath).normalize();
        if (normalizedPath.isAbsolute()) {
            return "错误：路径必须是相对路径";
        }

        // 文件路径 /tmp/vue_projects/vue_appid/relativeFilePath
        String filePath = AppConstant.CODE_OUTPUT_ROOT_DIR+"/vue_projects/vue_"+appId+"/" +normalizedPath;
       // 转换为Path对象
        Path filePathNew = Paths.get(filePath);

        // 确保父目录存在
        Path parentDir = filePathNew.getParent();
        if (parentDir != null) {
                Files.createDirectories(parentDir);
        }
            // 写入文件（UTF-8编码）
            Files.writeString(
                    filePathNew,
                    content,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
            return "文件写入成功：" + filePath;
        } catch (IOException e) {
            log.error("写入文件失败："+e.getMessage(),",文件路径："+relativeFilePath);
        }

        return "文件写入成功："+relativeFilePath;


    }
}
