package com.aetherbuilder.nocode.model.dto.app;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


import java.io.Serializable;

/**
 * 创建应用请求
 */
@Data
public class AppAddRequest implements Serializable {

    /**
     * 应用初始化的 prompt
     */
    private String initPrompt;

    private static final long serialVersionUID = 1L;
}
