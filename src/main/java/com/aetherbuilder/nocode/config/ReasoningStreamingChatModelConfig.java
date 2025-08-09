package com.aetherbuilder.nocode.config;

import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "langchain4j.open-ai.chat-model")
@Data
public class ReasoningStreamingChatModelConfig {

    private String baseUrl;

    private String apiKey;

    /**
     * 推理流式模型（用于 Vue 项目生成，带工具调用）
     */
    @Bean
    public StreamingChatModel reasoningStreamingChatModel() {
        // 设置模型名称和最大令牌数，这里使用测试环境配置
        final String modelName = "deepseek-chat"; // 测试环境使用的模型名称
        final int maxTokens = 8192; // 测试环境设置的最大令牌数

        // 生产环境应使用以下配置（当前被注释掉）
        // final String modelName = "deepseek-reasoner"; // 生产环境模型名称
        // final int maxTokens = 32768; // 生产环境最大令牌数

        // 使用OpenAiStreamingChatModel.builder()来构建模型实例
        return OpenAiStreamingChatModel.builder()
                .apiKey(apiKey) // 设置API密钥
                .baseUrl(baseUrl) // 设置基础URL
                .modelName(modelName) // 设置模型名称
                .maxTokens(maxTokens) // 设置最大令牌数
                .logRequests(true) // 启用请求日志记录
                .logResponses(true) // 启用响应日志记录
                .build(); // 构建并返回模型实例
    }
}