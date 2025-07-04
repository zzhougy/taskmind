package com.webmonitor.service.observer;

import com.webmonitor.core.WebContent;

import java.util.List;

public interface WebObserver {
  void send(List<WebContent> webContents);
} 