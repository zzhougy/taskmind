package com.webmonitor.service.springai;

import com.webmonitor.config.exception.BusinessException;
import com.webmonitor.service.AIService;
import com.webmonitor.util.CronUtil;
import jakarta.annotation.Nullable;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class TaskTools {

  public static final String TASK_SETTING_SUCCESS = "设置成功";
  public static final String TASK_SETTING_SUCCESS2 = "\"设置成功\"";
  public static final String TASK_SETTING_ERROR = "失败请修改描述重试或者联系管理员";
  @Resource
  @Lazy
  private AIService aiService;


//  @Tool(name = "获取当前时间。触发条件：输入内容包含“后”关键词", description = "触发条件：输入内容包含“后”")
//  String getCurrentTime() {
//    log.info("[getCurrentTime]");
//    return LocalDateTime.now().toString();
//  }

  @Tool(name = "设置定时提醒或者执行任务",
          description = "触发条件：输入内容包含“提醒”关键词。设置定时提醒或者执行任务：1) 简单提醒任务 2) 动态获取任务。对于网页内容获取任务，需在content参数中明确指定操作指令",
          returnDirect = true)
  String setTimingTask(ToolContext toolContext,

          @ToolParam(description = "仅动态获取任务需要，如'https://top.baidu.com/board?tab=realtime'。简单提醒任务留空", required = false)
          String url,

                       @Pattern(regexp = "minutely|hourly|daily|weekly|monthly|yearly", message = "频率类型错误")
                       @ToolParam(description = "任务执行频率：minutely|hourly|daily|weekly|monthly|yearly")
                       String frequency,

          /* 通用字段：hour/minute 在 daily/weekly/monthly/yearly 频率时必须提供 */
                       @ToolParam(description = "小时(0-23)，daily/weekly/monthly/yearly 频率时必须", required = false)
                       Integer hour,

                       @Nullable
                       @ToolParam(description = "分钟(0-59)，daily/weekly/monthly/yearly 频率时必须", required = false)
                       Integer minute,

          /* monthly 频率必须提供 day；yearly 频率必须提供 month 与 day */
                       @Nullable
                       @ToolParam(description = "日期(1-31)，monthly/yearly 频率时必须", required = false)
                       Integer day,

                       @Nullable
                       @ToolParam(description = "月份(1-12)，yearly 频率时必须", required = false)
                       Integer month,

          /* 星期几(1-7 对应周一到周日)，仅 weekly 频率需要 */
                       @Nullable
                       @ToolParam(description = "星期几(1-7)，weekly 频率时必须", required = false)
                       Integer dayOfWeek,

          /* 间隔时间：minutely(1-59) / hourly(1-23) 频率时必须 */
                       @Nullable
                       @ToolParam(description = "间隔分钟(1-59)，minutely 频率时必须；或间隔小时(1-23)，hourly 频率时必须", required = false)
                       Integer interval,

                       @ToolParam(description = "任务内容描述："
                  + "1) 简单提醒任务 - 直接填写提醒内容（如'吃药'）"
                  + "2) 动态获取任务 - 填写操作指令格式：'描述'，如'获取热搜标题'")
          String content
  ) {
    String userInput = (String) toolContext.getContext().get("userInput");

    log.info("setTimingTask invoked: " +
                    "url={}, frequency={}, hour={}, minute={}, day={}, month={}, " +
                    "dayOfWeek={}, interval={}, content={}",
            url, frequency, hour, minute, day, month, dayOfWeek, interval, content);
    try {
      // todo check
      String s = CronUtil.generateCronExpression(frequency, hour, minute, month, day, interval, dayOfWeek);
      log.info("cron={}", s);
//      aiService.setUpTimingTask(userInput, url, cron, content);
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