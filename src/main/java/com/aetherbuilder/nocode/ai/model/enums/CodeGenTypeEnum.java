package com.aetherbuilder.nocode.ai.model.enums;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

/**
 * @author : Cammy.Wu
 * Description : 
 */

@Getter
public enum CodeGenTypeEnum {

    HTML("原生 HTML 模式", "html"),
    MULTI_FILE("原生多个文件模式", "multi_file"),
    VUE_PROJECT("Vue 工程模式", "vue_project");

    private final String text;
    private final String value;

    CodeGenTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据给定的值获取对应的CodeGenTypeEnum枚举实例
     *
     * @param value 枚举的value值，用于匹配
     * @return 匹配到的CodeGenTypeEnum枚举对象，如果value为空或未找到匹配项则返回null
     */
    public static CodeGenTypeEnum getEnumByValue(String value) {
        // 检查传入的value是否为空（包括null或空字符串）
        if (ObjectUtil.isEmpty(value)) {
            return null;
        }
        // 遍历所有的CodeGenTypeEnum枚举值
        for (CodeGenTypeEnum codeGenTypeEnum : CodeGenTypeEnum.values()) {
            // 如果当前枚举的value与传入的value相等，则返回该枚举实例
            if (codeGenTypeEnum.getValue().equals(value)) {
                return codeGenTypeEnum;
            }
        }
        // 如果未找到匹配的枚举，则返回null
        return null;
    }

}
