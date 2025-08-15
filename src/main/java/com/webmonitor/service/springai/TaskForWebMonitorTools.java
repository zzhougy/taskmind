package com.webmonitor.service.springai;

import com.webmonitor.config.exception.BusinessException;
import com.webmonitor.service.AIService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class TaskForWebMonitorTools {

  public static final String TASK_SETTING_SUCCESS = "操作完成，请前往查看结果";
  public static final String TASK_SETTING_SUCCESS2 = "\"操作完成，请前往查看结果\"";
  public static final String TASK_SETTING_ERROR = "失败请修改描述重试或者联系管理员";
  @Resource
  @Lazy
  private AIService aiService;



  @Tool(name = "设置任务执行",
          description = "设置任务执行",
          returnDirect = true)
  String setTimingTask(ToolContext toolContext,

          @ToolParam(description = "输入的url", required = true)
          String url,
                       @ToolParam(description = "输入的css选择器", required = true)
                       String cssSelector,

                       @ToolParam(description = "输入的xpath", required = true)
                       String xpath,

                       @ToolParam(description = "不含执行时间或者执行频率的任务内容描述(注意：禁止删减用户表达的意思)：")
          String content
  ) {
    String userInput = (String) toolContext.getContext().get("userInput");
    String cron = (String) toolContext.getContext().get("cron");


    log.info("setTimingTask invoked: userInput={}, " +
                    "url={}, cron={}, content={}, cssSelector={}, xpath={}",
            userInput, url, cron, content, cssSelector, xpath);
    try {
      aiService.setUpTimingTaskWebMonitor( cron, url, cssSelector, xpath);
    } catch (BusinessException e) {
      log.error("[setTimingTask] error: ", e);
      return e.getMessage();
    } catch (Exception e) {
      log.error("[setTimingTask] error: ", e);
      return TASK_SETTING_ERROR;
    }
    return TASK_SETTING_SUCCESS;
  }


}