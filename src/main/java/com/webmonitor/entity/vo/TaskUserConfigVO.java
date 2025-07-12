package com.webmonitor.entity.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskUserConfigVO {

  private Integer id;

  // 任务执行时间
  private String executionTime;

  private String url;

  private String taskContent;

  private Boolean enable;

  private String userInput;

  private LocalDateTime createTime;

  private LocalDateTime updateTime;

}