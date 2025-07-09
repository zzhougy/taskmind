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
@AutoTable(comment = "任务执行记录表")
@TableName("task_user_record")
public class TaskUserRecord {

  @PrimaryKey(autoIncrement = true)
  private Long id;

  @AutoColumn(comment = "用户id")
  private Long userId;

  @AutoColumn
  private Long taskConfigId;

  @AutoColumn(length = 50)
  private String contentTitle;

  @AutoColumn(length = 50)
  private String contentDetail;

  @AutoColumn(comment = "链接（如有）", length = 50)
  private String contentUrl;

  @AutoColumn(comment = "时间", length = 50)
  private String contentDateStr;

  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

}