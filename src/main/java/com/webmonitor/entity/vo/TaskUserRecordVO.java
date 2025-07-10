package com.webmonitor.entity.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskUserRecordVO {

  private String contentTitle;

  private String contentDetail;

  private String contentUrl;

  private String contentDateStr;

  private LocalDateTime createTime;

}