package com.webmonitor.constant;

import lombok.Getter;

@Getter
public enum WayToGetHtmlEnum {
  JSOUP("jsoup"),
  SELENIUM("selenium"),
  ;

  private String code;

  WayToGetHtmlEnum(String code) {
    this.code = code;
  }

}
