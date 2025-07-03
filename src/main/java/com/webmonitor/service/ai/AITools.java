package com.webmonitor.service.ai;

import org.springframework.ai.tool.annotation.Tool;

public class AITools {

  @Tool(description = "Set the timing task according to the user 's content description")
  boolean setUpTimingTask(String url, String cron, String contentWithoutTimeAndUrl) {
    return true;
  }

}