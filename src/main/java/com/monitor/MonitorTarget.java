package com.monitor;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class MonitorTarget {
    private String name;            // 监控名称
    private String url;             // 监控地址
    private String articleSelector; // 文章列表的CSS选择器
    private String titleSelector;   // 标题的CSS选择器
    private String dateSelector;    // 日期的CSS选择器
    private int checkInterval;      // 检查间隔（分钟）
} 