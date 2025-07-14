package com.webmonitor.entity.bo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

// todo 加校验
@Data
public class TaskUserConfigCreateBO {

  @NotBlank(message = "提醒内容不能为空")
  private String taskContent;

  @NotBlank(message = "频率类型不能为空")
  @Pattern(regexp = "minutely|hourly|daily|weekly|monthly|yearly", message = "频率类型错误")
  private String frequency;

  private Integer hour;
  private Integer minute;

  private Integer month; // 1-12，仅yearly频率需要
  private Integer day;   // 1-31，monthly/yearly频率需要
  private Integer interval; // 间隔时间，hourly(1-23)或minutely(1-59)频率需要

  // todo 加校验
  private Integer dayOfWeek;


  //    @AssertTrue(message = "monthly频率需要指定日期(1-31)")
  public boolean isMonthlyDayValid() {
    if ("monthly".equals(frequency)) {
      return day != null && day >= 1 && day <= 31;
    }
    return true;
  }

  //    @AssertTrue(message = "yearly频率需要指定月份(1-12)和日期(1-31)")
  public boolean isYearlyDateValid() {
    if ("yearly".equals(frequency)) {
      return month != null && month >= 1 && month <= 12 &&
              day != null && day >= 1 && day <= 31;
    }
    return true;
  }

  //    @AssertTrue(message = "minutely频率需要指定间隔时间(1-59)")
  public boolean isMinutelyIntervalValid() {
    if ("minutely".equals(frequency)) {
      return interval != null && interval >= 1 && interval <= 59;
    }
    return true;
  }

  //    @AssertTrue(message = "daily/weekly/monthly/yearly频率需要指定小时和分钟")
  public boolean isHourMinuteRequired() {
    if ("daily".equals(frequency) || "weekly".equals(frequency) || "monthly".equals(frequency) || "yearly".equals(frequency)) {
      return hour != null && hour >= 0 && hour <= 23 &&
              minute != null && minute >= 0 && minute <= 59;
    }
    return true;
  }
}