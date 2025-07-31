package com.webmonitor.constant;

import lombok.Getter;

@Getter
public enum WayToGetHtmlEnum {
  JSOUP("jsoup"),
  SELENIUM("selenium"),
  PLAYWRIGHT("playwright"),
  ;

  private String code;

  WayToGetHtmlEnum(String code) {
    this.code = code;
  }


  public static WayToGetHtmlEnum getByCode(String code) {
    for (WayToGetHtmlEnum value : values()) {
      if (value.code.equals(code)) {
        return value;
      }
    }
    return null;
  }
}
