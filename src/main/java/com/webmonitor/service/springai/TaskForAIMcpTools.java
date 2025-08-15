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
public class TaskForAIMcpTools {

  public static final String TASK_SETTING_SUCCESS = "操作完成，请前往查看结果";
  public static final String TASK_SETTING_SUCCESS2 = "\"操作完成，请前往查看结果\"";
  public static final String TASK_SETTING_ERROR = "失败请修改描述重试或者联系管理员";
  @Resource
  @Lazy
  private AIService aiService;


//  @Tool(name = "获取当前时间", description = "获取当前时间")
//  String getCurrentTime() {
//    log.info("====[getCurrentTime]");
//    return LocalDateTime.now().toString();
//  }

  @Tool(name = "设置定时提醒或者执行任务",
          description = "设置定时提醒或者执行任务",
          returnDirect = true)
  String setTimingTask(ToolContext toolContext,

          @ToolParam(description = "输入的url", required = false)
          String url,

                       @ToolParam(description = "是否需要联网获取网站的实时数据", required = true)
                       boolean isNeedNetData,

                       @ToolParam(description = "不含执行时间或者执行频率的任务内容描述(注意：禁止删减用户表达的意思)：")
          String content
  ) {
    String userInput = (String) toolContext.getContext().get("userInput");
    String cron = (String) toolContext.getContext().get("cron");

    log.info("setTimingTask invoked: userInput={}, " +
                    "isNeedNetData={}, cron={}, content={}",
            userInput, isNeedNetData, cron, content);
    try {
      aiService.setUpTimingTask(userInput, isNeedNetData, cron, content);
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