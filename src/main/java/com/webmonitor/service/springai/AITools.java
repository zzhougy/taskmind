package com.webmonitor.service.springai;

import com.webmonitor.config.WebMonitorFactory;
import com.webmonitor.config.exception.BusinessException;
import com.webmonitor.core.WebMonitor;
import com.webmonitor.service.AIService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ToolContext;
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

  @Tool(description = "设置定时任务：1) 简单提醒任务 2) 动态获取任务。对于网页内容获取任务，需在content参数中明确指定操作指令。",
          returnDirect = true)
  String setUpTimingTask(ToolContext toolContext,
          @Nullable
          @ToolParam(description = "仅动态获取任务需要，如'https://top.baidu.com/board?tab=realtime'。简单提醒任务留空", required = false)
          String url,

                        @ToolParam( description = "标准6字段cron表达式，必须包含秒、分、时、日、月、周。" +
                                 "示例: '0 0 10 * * ?'表示每天10点；")
          String cron,

          @ToolParam(description = "任务内容描述："
                  + "1) 简单提醒任务 - 直接填写提醒内容（如'吃药'）"
                  + "2) 动态获取任务 - 填写操作指令格式：'描述'，如'获取热搜标题'")
          String content
  ) {
    String userInput = (String) toolContext.getContext().get("userInput");
    log.info("[setUpTimingTask] userInput: {}, url: {}, cron: {}, content: {}", userInput, url, cron, content);
    try {
      aiService.setUpTimingTask(userInput, url, cron, content);
    } catch (BusinessException e) {
      log.error("[setUpTimingTask] error: ", e);
      return e.getMessage();
    } catch (Exception e) {
      log.error("[setUpTimingTask] error: ", e);
      return "失败请修改描述重试或者联系管理员";
    }
    return "设置成功";
  }

}