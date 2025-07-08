package com.webmonitor.service;

public interface AIService {
  boolean setUpTimingTask(String url, String cron, String content) throws Exception;

}
