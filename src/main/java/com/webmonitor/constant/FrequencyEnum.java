package com.webmonitor.constant;

import lombok.Getter;

@Getter
public enum FrequencyEnum {

  ONCE("once", "一次性任务"),
  PER_SECOND("perSecond", "每秒执行一次"),
  MINUTELY("minutely", "每分钟执行一次"),
  HOURLY("hourly", "每小时执行一次"),
  DAILY("daily", "每天执行一次"),
  WEEKLY("weekly", "每周执行一次"),
  MONTHLY("monthly", "每月执行一次"),
  YEARLY("yearly", "每年执行一次"),
  ;

  private String code;
  private String description;

  FrequencyEnum(String code, String description) {
    this.code = code;
    this.description = description;
  }

}
