package com.webmonitor.entity.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.dromara.autotable.annotation.AutoColumn;
import org.dromara.autotable.annotation.AutoTable;
import org.dromara.autotable.annotation.PrimaryKey;

import java.time.LocalDateTime;

@Data
@AutoTable(comment = "任务配置表")
@TableName("task_user_config")
public class TaskUserConfig {

  @PrimaryKey(autoIncrement = true)
  private Integer id;

  @AutoColumn(comment = "用户id")
  private Integer userId;

  @AutoColumn(comment = "cron表达式", length = 20, notNull = true)
  private String cronExpression;

  @AutoColumn(comment = "wayToGetHtmlCode", length = 10)
  private String wayToGetHtmlCode;

//  @AutoColumn(comment = "任务执行间隔时间（秒），只执行", length = 10)
//  private String executionIntervalTime;

  @AutoColumn(comment = "taskTypeCode", length = 10, notNull = true)
  private String taskTypeCode;

  @AutoColumn(comment = "用户的输入", notNull = true)
  private String userInput;

  @AutoColumn(comment = "url")
  private String url;

  @AutoColumn(comment = "cssSelector")
  private String cssSelector;

  @AutoColumn(comment = "xpathSelector")
  private String xpathSelector;

  @AutoColumn(comment = "taskContent", length = 50, notNull = true)
  private String taskContent;

  @AutoColumn(comment = "是否启用", notNull = true, defaultValue = "true")
  private Boolean enable;

  @AutoColumn(comment = "是否删除", notNull = true, defaultValue = "false")
  private Boolean deleted;

  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updateTime;

}