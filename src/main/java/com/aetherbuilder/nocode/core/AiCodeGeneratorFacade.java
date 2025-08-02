package com.aetherbuilder.nocode.core;

import com.aetherbuilder.nocode.ai.AiCodeGeneratorService;
import com.aetherbuilder.nocode.ai.model.HtmlCodeResult;
import com.aetherbuilder.nocode.ai.model.MultiFileCodeResult;
import com.aetherbuilder.nocode.ai.model.enums.CodeGenTypeEnum;
import com.aetherbuilder.nocode.exception.BusinessException;
import com.aetherbuilder.nocode.exception.ErrorCode;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;

/**
 * AI 代码生成外观类，组合生成和保存功能
 */
@Service
public class AiCodeGeneratorFacade {

    private static final Logger log = LoggerFactory.getLogger(AiCodeGeneratorFacade.class);
    @Resource
    private AiCodeGeneratorService aiCodeGeneratorService;

    /**
     * 统一入口：根据类型生成并保存代码
     *
     * @param userMessage     用户提示词
     * @param codeGenTypeEnum 生成类型
     * @return 保存的目录
     */
    public File generateAndSaveCode(String userMessage, CodeGenTypeEnum codeGenTypeEnum) {
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成类型为空");
        }
        return switch (codeGenTypeEnum) {
            case HTML -> generateAndSaveHtmlCode(userMessage);
            case MULTI_FILE -> generateAndSaveMultiFileCode(userMessage);
            default -> {
                String errorMessage = "不支持的生成类型：" + codeGenTypeEnum.getValue();
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, errorMessage);
            }
        };
    }

    /**
     * 生成 HTML 模式的代码并保存
     *
     * @param userMessage 用户提示词
     * @return 保存的目录
     */
    private File generateAndSaveHtmlCode(String userMessage) {
        HtmlCodeResult result = aiCodeGeneratorService.generateHtmlCode(userMessage);
        return CodeFileSaver.saveHtmlCodeResult(result);
    }

    /**
     * 生成多文件模式的代码并保存
     *
     * @param userMessage 用户提示词
     * @return 保存的目录
     */
    private File generateAndSaveMultiFileCode(String userMessage) {
        MultiFileCodeResult result = aiCodeGeneratorService.generateMultiFileCode(userMessage);
        return CodeFileSaver.saveMultiFileCodeResult(result);
    }

    /**
     * 生成 HTML 模式的代码并保存（流式）
     *
     * @param userMessage 用户提示词
     * @return 保存的目录
     */
    private Flux<String> generateAndSaveHtmlCodeStream(String userMessage) {
        // 调用 AI 代码生成服务，获取流式生成的 HTML 代码片段
        Flux<String> result = aiCodeGeneratorService.generateHtmlCodeStream(userMessage);

        // 用于在流式传输过程中暂存所有生成的代码片段
        StringBuilder codeBuilder = new StringBuilder();

        // 返回处理后的流，在数据生成和完成时执行附加操作
        return result
                // 每接收到一个代码片段时触发，实时将代码片段追加到缓冲区中
                .doOnNext(chunk -> {
                    // 实时收集代码片段
                    codeBuilder.append(chunk);
                })
                // 当整个流式生成过程完成时触发，进行后续的代码保存操作
                .doOnComplete(() -> {
                    // 流式返回完成后保存代码
                    try {
                        // 将收集到的所有代码片段拼接成完整的 HTML 代码
                        String completeHtmlCode = codeBuilder.toString();
                        // 解析完整的 HTML 代码，生成包含结构化信息的对象
                        HtmlCodeResult htmlCodeResult = CodeParser.parseHtmlCode(completeHtmlCode);
                        // 将解析后的代码结果保存到文件系统中，并获取保存的目录
                        File saveDir = CodeFileSaver.saveHtmlCodeResult(htmlCodeResult);
                        // 打印日志，记录保存成功及保存路径
                        log.info("保存成功，路径为：" + saveDir.getAbsolutePath());
                    } catch (Exception e) {
                        // 捕获并打印保存过程中可能发生的异常
                        log.error("保存失败: {}", e.getMessage());
                    }
                });
    }

    /**
     * 生成多文件模式的代码并保存（流式）
     *
     * @param userMessage 用户提示词
     * @return 保存的目录
     */
    // 根据用户输入生成并返回多文件代码流，同时将完整代码保存到本地
    private Flux<String> generateAndSaveMultiFileCodeStream(String userMessage) {
        // 调用服务生成多文件代码的流式数据（可能是分块传输）
        Flux<String> result = aiCodeGeneratorService.generateMultiFileCodeStream(userMessage);

        // 用于拼接流中每次传来的代码块，最终组合成完整的代码内容
        StringBuilder codeBuilder = new StringBuilder();

        return result
                // 每接收到一个代码块（chunk），就将其追加到 codeBuilder 中
                .doOnNext(chunk -> {
                    codeBuilder.append(chunk);
                })
                // 当流结束时（所有代码块接收完毕），执行保存操作
                .doOnComplete(() -> {
                    try {
                        // 将所有代码块拼接成完整的代码字符串
                        String completeMultiFileCode = codeBuilder.toString();
                        // 解析完整代码，分离出各个文件的内容及结构信息
                        MultiFileCodeResult multiFileCodeResult = CodeParser.parseMultiFileCode(completeMultiFileCode);
                        // 将解析后的多文件代码结果保存到磁盘，并返回保存目录
                        File saveDir = CodeFileSaver.saveMultiFileCodeResult(multiFileCodeResult);
                        // 打印保存成功的日志，包含保存路径
                        log.info("保存成功，路径为：" + saveDir.getAbsolutePath());
                    } catch (Exception e) {
                        // 捕获保存过程中可能出现的任何异常，并记录错误日志
                        log.error("保存失败: {}", e.getMessage());
                    }
                });
    }

    /**
     * 统一入口：根据类型生成并保存代码（流式）
     *
     * @param userMessage     用户提示词
     * @param codeGenTypeEnum 生成类型
     */
    public Flux<String> generateAndSaveCodeStream(String userMessage, CodeGenTypeEnum codeGenTypeEnum) {
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成类型为空");
        }
        return switch (codeGenTypeEnum) {
            case HTML -> generateAndSaveHtmlCodeStream(userMessage);
            case MULTI_FILE -> generateAndSaveMultiFileCodeStream(userMessage);
            default -> {
                String errorMessage = "不支持的生成类型：" + codeGenTypeEnum.getValue();
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, errorMessage);
            }
        };
    }


}
