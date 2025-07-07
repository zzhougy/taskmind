package com.webmonitor.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.dromara.autotable.annotation.AutoColumn;
import org.dromara.autotable.annotation.AutoTable;
import org.dromara.autotable.annotation.PrimaryKey;

@Data
@AutoTable(comment = "用户表")
@TableName("user")
public class User extends BaseEntity {

  @PrimaryKey(autoIncrement = true)
  private Long id;

  @AutoColumn(value = "open_id", comment = "用户唯一标识", length = 50)
  private String openId;

  @AutoColumn(length = 50)
  private String username;

}