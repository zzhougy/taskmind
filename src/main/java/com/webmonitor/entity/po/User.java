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
@AutoTable(comment = "用户表")
@TableName("user")
public class User {

  @PrimaryKey(autoIncrement = true)
  private Long id;

  @AutoColumn(value = "open_id", comment = "微信的用户唯一标识", length = 50)
  private String openId;

  @AutoColumn(length = 50)
  private String username;

  @AutoColumn(comment = "是否启用", notNull = true, defaultValue = "true")
  private Boolean enable;

  @AutoColumn(comment = "是否删除", notNull = true, defaultValue = "false")
  private Boolean deleted;

  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updateTime;
}