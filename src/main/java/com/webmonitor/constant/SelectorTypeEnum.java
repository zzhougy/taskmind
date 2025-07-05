package com.webmonitor.constant;

import lombok.Getter;

@Getter
public enum SelectorTypeEnum {
  CSS("css"),
  XPATH("xpath"),
  ;

  private String code;

  SelectorTypeEnum(String code) {
    this.code = code;
  }

}
