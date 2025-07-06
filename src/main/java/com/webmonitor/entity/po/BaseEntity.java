package com.webmonitor.entity.po;

import lombok.Data;

import java.util.Date;

@Data
public abstract class BaseEntity {

    private Long id;

    private Date createTime;

    private Date updateTime;

}