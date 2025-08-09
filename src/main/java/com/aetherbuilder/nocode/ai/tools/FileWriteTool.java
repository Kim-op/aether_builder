package com.aetherbuilder.nocode.ai.tools;

import com.aetherbuilder.nocode.constant.AppConstant;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Slf4j
public class FileWriteTool {

    @Tool("写入文件到指定路径")
    public String writeFile(
            @P("文件的相对路径")
            String relativeFilePath,
            @P("要写入文件的内容")
            String content,
            @ToolMemoryId Long appId
    ) {
        try {
            // 获取文件的路径
            Path path = Paths.get(relativeFilePath);
            // 判断路径是否为绝对路径
            if (!path.isAbsolute()) {
                // 相对路径处理，创建基于 appId 的项目目录
                // 创建基于 appId 的项目目录
                String projectDirName = "vue_project_" + appId;
                // 获取项目根目录
                Path projectRoot = Paths.get(AppConstant.CODE_OUTPUT_ROOT_DIR, projectDirName);
                // 解析相对路径，获取绝对路径
                path = projectRoot.resolve(relativeFilePath);
            }
            // 创建父目录（如果不存在）
            // 获取父目录
            Path parentDir = path.getParent();
            if (parentDir != null) {
                // 创建父目录
                Files.createDirectories(parentDir);
            }
            // 写入文件内容
            Files.write(path, content.getBytes(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
            // 记录日志，文件写入成功
            log.info("成功写入文件: {}", path.toAbsolutePath());
            // 注意要返回相对路径，不能让 AI 把文件绝对路径返回给用户
            return "文件写入成功: " + relativeFilePath;
        } catch (IOException e) {
            // 记录错误日志，并返回错误信息
            String errorMessage = "文件写入失败: " + relativeFilePath + ", 错误: " + e.getMessage();
            log.error(errorMessage, e);
            return errorMessage;
        }
    }

}