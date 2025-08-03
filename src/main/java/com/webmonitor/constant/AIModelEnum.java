package com.webmonitor.constant;


public enum AIModelEnum {
  ZHIPU_GLM4_FLASH("zhipuGlm4Flash"),
  ZHIPU_GLM45_FLASH("zhipuGlm45Flash"),
  ZHIPU_GLM4_PLUS("zhipuGlm4Plus"),
  ZHIPU_GLM4_AIR("zhipuGlm4Air"), // 处理mcp-playwright效果一般
  ZHIPU_GLM4_FLASH_X("zhipuGlm4FlashX"), // 处理mcp-playwright效果一般
  ZHIPU_GLMZ1_FLASH("zhipuGlmZ1Flash"), // 免费的推理模型。无法使用mcp-playwright
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
