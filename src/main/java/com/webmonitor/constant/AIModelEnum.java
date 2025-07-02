package com.webmonitor.constant;


public enum AIModelEnum {
  ZHIPU("zhipu"),
  KIMI("kimi"),
  DEEPSEEK("deepseek"),
  CUSTOM("custom")
    ;

  private String name;

  AIModelEnum(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

}
