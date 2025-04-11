package com.webmonitor.core;

import com.webmonitor.WebMonitorEnum;

import java.util.List;

public interface ContentFetcher {
    List<WebContent> fetch() throws Exception;
    WebMonitorEnum getWebMonitorEnum();
} 