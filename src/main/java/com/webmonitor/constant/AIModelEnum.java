package com.webmonitor.constant;


public enum AIModelEnum {
  ZHIPU_GLM4_FLASH("zhipuGlm4Flash"), // GLM-4-Flash-250414：免费。
  ZHIPU_GLM45_FLASH("zhipuGlm45Flash"), // GLM-4.5-Flash：免费。强大推理能力、稳定代码生成和多工具协同处理能力
  ZHIPU_GLM4_PLUS("zhipuGlm4Plus"), // glm-4-plus
  ZHIPU_GLM4_AIR("zhipuGlm4Air"), // 处理mcp-playwright效果一般
  ZHIPU_GLM4_FLASH_X("zhipuGlm4FlashX"), // 处理mcp-playwright效果一般
  ZHIPU_GLMZ1_FLASH("zhipuGlmZ1Flash"), // GLM-Z1-Flash：免费的推理模型。无法使用mcp-playwright
  ZHIPU_GLM4V_FLASH("zhipuGlm4VFlash"), // GLM-4V-Flash：免费，无法使用mcp-playwright。图像理解模型
  ZHIPU_GLM41V_THINKING_FLASH("zhipuGlm41VThinkingFlash"), // GLM-4.1V-Thinking-Flash：免费视觉推理模型，无法使用mcp-playwright
  ZHIPU_COGVIEW3_FLASH("zhipuCogview3Flash"), // cogview-3-flash：免费图像生成模型
  KIMI("kimi"),
  DEEPSEEK("deepseek"),
  CUSTOM("custom"),
  GEMINI("gemini"),
    ;

  private String name;

  AIModelEnum(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public static AIModelEnum getByName(String name) {
    for (AIModelEnum value : values()) {
      if (value.getName().equals(name)) {
        return value;
      }
    }
    return null;
  }

}