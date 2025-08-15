package com.webmonitor.service;

import com.webmonitor.entity.bo.AIUserInputBO;

public interface AIService {
  void setUpTimingTask(String userInput, boolean isNeedNetData, String cron, String content) throws Exception;

  String chatWithAIMcp(AIUserInputBO bo);

  String chatWithAIEntrance(AIUserInputBO bo);

  String chatWithAIForMonitor(AIUserInputBO bo);

  String setUpTimingTaskWebMonitor(String cron, String url, String cssSelector, String xPath) throws Exception;
}
