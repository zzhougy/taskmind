package com.webmonitor.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.dromara.autotable.annotation.AutoColumn;
import org.dromara.autotable.annotation.AutoTable;
import org.dromara.autotable.annotation.PrimaryKey;

@Data
@AutoTable(comment = "任务执行记录表")
@TableName("task_user_record")
public class TaskUserRecord extends BaseEntity {

  @PrimaryKey(autoIncrement = true)
  private Long id;

  @AutoColumn(comment = "用户id")
  private Long userId;

  @AutoColumn(length = 50)
  private String taskConfigId;

  @AutoColumn(length = 50)
  private String contentTitle;

  @AutoColumn(length = 50)
  private String contentDetail;

  @AutoColumn(comment = "链接（如有）", length = 50)
  private String contentLink;

  @AutoColumn(comment = "时间", length = 50)
  private String contentDateStr;


}