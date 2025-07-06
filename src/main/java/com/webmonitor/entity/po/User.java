package com.webmonitor.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.dromara.autotable.annotation.AutoColumn;
import org.dromara.autotable.annotation.AutoTable;

@Data
@AutoTable
@TableName("user")
public class User extends BaseEntity {
//    @TableField("open_id")
    @AutoColumn
    /**
     * 长期授权字符串
     */
    private String openId;

    @AutoColumn
    private String username;

}