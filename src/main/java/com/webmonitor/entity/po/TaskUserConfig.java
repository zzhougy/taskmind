package com.webmonitor.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.dromara.autotable.annotation.AutoColumn;
import org.dromara.autotable.annotation.AutoTable;
import org.dromara.autotable.annotation.PrimaryKey;

@Data
@AutoTable(comment = "任务配置表")
@TableName("task_user_config")
public class TaskUserConfig extends BaseEntity {

  @PrimaryKey(autoIncrement = true)
  private Long id;

  @AutoColumn(comment = "用户id")
  private Long userId;

  @AutoColumn(comment = "cron表达式", length = 10, notNull = true)
  private String cronExpression;

//  @AutoColumn(comment = "任务执行间隔时间（秒），只执行", length = 10)
//  private String executionIntervalTime;

  @AutoColumn(comment = "是否启用", notNull = true, defaultValue = "true")
  private Boolean enable;

}