package com.webmonitor.entity.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import org.dromara.autotable.annotation.AutoColumn;

import java.time.LocalDateTime;

@Data
public abstract class BaseEntity {

  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updateTime;

  @AutoColumn(comment = "是否删除", notNull = true, defaultValue = "false")
  private Boolean deleted;
}