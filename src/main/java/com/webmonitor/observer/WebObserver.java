package com.webmonitor.observer;

import com.webmonitor.core.WebContent;

import java.util.List;

public interface WebObserver {
    void update(List<WebContent> webContents);
} 