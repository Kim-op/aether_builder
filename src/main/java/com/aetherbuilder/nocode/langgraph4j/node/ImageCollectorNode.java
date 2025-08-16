package com.aetherbuilder.nocode.langgraph4j.node;

import com.aetherbuilder.nocode.langgraph4j.ai.ImageCollectionService;
import com.aetherbuilder.nocode.langgraph4j.model.ImageResource;
import com.aetherbuilder.nocode.langgraph4j.model.enums.ImageCategoryEnum;
import com.aetherbuilder.nocode.langgraph4j.state.WorkflowContext;
import com.aetherbuilder.nocode.langgraph4j.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.AsyncNodeAction;
import org.bsc.langgraph4j.prebuilt.MessagesState;

import java.util.Arrays;
import java.util.List;

import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

@Slf4j
public class ImageCollectorNode {
    public static AsyncNodeAction<MessagesState<String>> create() {
        return node_async(state -> {
            WorkflowContext context = WorkflowContext.getContext(state);
            String originalPrompt = context.getOriginalPrompt();
            String imageListStr = "";
            
//            // 简单的假数据
//            List<ImageResource> imageList = Arrays.asList(
//                ImageResource.builder()
//                    .category(ImageCategoryEnum.CONTENT)
//                    .description("假数据图片1")
//                    .url("https://cammy-1327275726.cos.ap-guangzhou.myqcloud.com/avatar/2f0bafa0782b2c24af5a369164cd34d.png")
//                    .build(),
//                ImageResource.builder()
//                    .category(ImageCategoryEnum.LOGO)
//                    .description("假数据图片2")
//                    .url("https://cammy-1327275726.cos.ap-guangzhou.myqcloud.com/avatar/2f0bafa0782b2c24af5a369164cd34d.png")
//                    .build()
//            );

            try {
                // 获取AI图片收集服务
                ImageCollectionService imageCollectionService = SpringContextUtil.getBean(ImageCollectionService.class);
                // 使用 AI 服务进行智能图片收集
                imageListStr = imageCollectionService.collectImages(originalPrompt);
                imageCollectionService.collectImages(originalPrompt);
            } catch (Exception e) {
                log.error("图片收集失败: {}", e.getMessage(), e);
            }
            
//            // 更新状态
//            context.setCurrentStep("图片收集");
//            context.setImageList(imageList);
//            log.info("图片收集完成，共收集 {} 张图片", imageList.size());
//            return WorkflowContext.saveContext(context);

            // 更新状态
            context.setCurrentStep("图片收集");
            context.setImageListStr(imageListStr);
            return WorkflowContext.saveContext(context);
        });
    }
}