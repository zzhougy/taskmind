package com.webmonitor.constant;

import lombok.Getter;

@Getter
public enum TaskTypeEnum {
  SIMPLE("simple", "简单任务"),
  XPATH_SELECTOR("xpathSelector", "xpathSelector"),
  CSS_SELECTOR("cssSelector", "cssSelector"),
  ;

  private String code;
  private String description;

  TaskTypeEnum(String code, String description) {
    this.code = code;
    this.description = description;
  }

}
