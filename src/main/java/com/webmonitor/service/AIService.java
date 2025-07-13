package com.webmonitor.service;

import com.webmonitor.entity.bo.AIUserInputBO;

public interface AIService {
  void setUpTimingTask(String userInput, String url, String cron, String content) throws Exception;

  String chatWithAI(AIUserInputBO bo);
}
