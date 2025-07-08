package com.webmonitor.service.springai;

import com.webmonitor.config.WebMonitorFactory;
import com.webmonitor.core.WebMonitor;
import com.webmonitor.service.AIService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class AITools {

  @Resource
  private WebMonitor monitor;
  @Resource
  private WebMonitorFactory webMonitorFactory;
  @Resource
  private AIService aiService;

  @Tool(description = "设置定时任务：1) 简单提醒任务 2) 动态获取任务。对于网页内容获取任务，需在content参数中明确指定操作指令（如GET_FIRST_HOT）。")
  boolean setUpTimingTask(
          @Nullable
          @ToolParam(description = "仅动态获取任务需要，如'https://top.baidu.com/board?tab=realtime'。简单提醒任务留空", required = false)
          String url,

          @ToolParam(description = "标准6字段cron表达式，必须包含秒、分、时、日、月、周。示例: '0 0 10 * * ?'表示每天10点")
          String cron,

          @ToolParam(description = "任务内容描述："
                  + "1) 简单提醒任务 - 直接填写提醒内容（如'吃药'）"
                  + "2) 动态获取任务 - 填写操作指令格式：'指令::描述'，如'GET_FIRST_HOT::获取热搜标题'")
          String content
  ) {
    log.info("[setUpTimingTask] url: {}, cron: {}, content: {}", url, cron, content);
    try {
      return aiService.setUpTimingTask(url, cron, content);
    } catch (Exception e) {
      log.error("[setUpTimingTask] error: {}", e.getMessage());
      return false;
    }
  }

}