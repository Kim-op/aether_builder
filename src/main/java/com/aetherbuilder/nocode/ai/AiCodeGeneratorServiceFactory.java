package com.aetherbuilder.nocode.ai;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiCodeGeneratorServiceFactory {

    @Resource
    private ChatModel chatModel;

    @Resource
    private StreamingChatModel streamingChatModel;

    // 定义一个Spring Bean，用于提供AiCodeGeneratorService实例
    @Bean
    public AiCodeGeneratorService aiCodeGeneratorService() {
        // 使用AiServices构建器创建AiCodeGeneratorService对象
        return AiServices.builder(AiCodeGeneratorService.class)
                // 设置聊天模型，提供给AI代码生成功能使用
                .chatModel(chatModel)
                // 设置流式聊天模型，用于支持实时返回生成内容
                .streamingChatModel(streamingChatModel)
                // 构建并返回AiCodeGeneratorService实例
                .build();
    }

}
