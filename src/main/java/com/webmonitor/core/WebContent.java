package com.webmonitor.core;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WebContent {
  // 唯一字段，用于判断是否有新的内容，必填
  private String id;
  private String title;
  private String description;
  private String url;
  private String source;
  private String dateStr;
  private String category;
} 