package com.webmonitor.entity.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskUserRecordVO {

  private String contentTitle;

  private String contentDetail;

  private String contentUrl;

  private String contentDateStr;

  private String userInput;

  private LocalDateTime createTime;

}