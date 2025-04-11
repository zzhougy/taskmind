package com.webmonitor.core;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WebContent {
    private String id;
    private String title;
    private String description;
    private String link;
    private String source;
    private String dateStr;
    private String category;
} 