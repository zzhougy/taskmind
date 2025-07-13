package com.webmonitor.service.springai;

import com.webmonitor.config.exception.BusinessException;
import com.webmonitor.service.AIService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.Nullable;
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
          @Nullable
          @ToolParam(description = "仅动态获取任务需要，如'https://top.baidu.com/board?tab=realtime'。简单提醒任务留空", required = false)
          String url,

                       @Nullable
                       @ToolParam( description = "-标准6字段cron表达式，必须包含秒、分、时、日、月、周。" +
//                               "-如果是距离当前时间之后的某个时间，则应该根据当前时间加上之后多久计算得到。" +
//                               "-如果是距离当前时间之后的某个时间，则秒、分、时、日、月应该都有数字。" +
                                 "-示例: '0 0 10 * * ?'表示每天10点；")
          String cron,

           @Nullable
           @ToolParam( description = "距离当前时间多久，单位为秒。如果没有提到秒或者时间是按照固定频率，该值为空。")
           Integer afterSeconds,


                       @Nullable
                       @ToolParam( description = "距离当前时间多久，单位为分钟。如果没有提到分钟或者时间是按照固定频率，该值为空。")
                       Integer afterMinutes,

                       @Nullable
                       @ToolParam( description = "距离当前时间多久，单位为小时。如果没有提到小时或者时间是按照固定频率，该值为空。")
                       Integer afterHours,

                       @Nullable
                       @ToolParam( description = "距离当前时间多久，单位为天。如果没有提到'天'或者时间是按照固定频率，该值为空。")
                       Integer afterDays,

                       @Nullable
                       @ToolParam( description = "距离当前时间多久，单位为月。如果没有提到月或者时间是按照固定频率，该值为空。")
                       Integer afterMonths,

                       @Nullable
                       @ToolParam( description = "距离当前时间多久，单位为年。如果没有提到年或者时间是按照固定频率，该值为空。")
                       Integer afterYears,

                       @Nullable
                       @ToolParam( description = "是在当天时间的哪一秒。如果没有提到秒，该值为空。")
                       Integer seconds,

                       @Nullable
                       @ToolParam( description = "是在当天时间的哪一分。如果没有提到分，该值为空。")
                       Integer minutes,

                       @Nullable
                       @ToolParam( description = "是在当天时间的哪一时。如果没有提到时，该值为空。")
                       Integer hours,

                       @Nullable
                       @ToolParam( description = "是在几号。如果没有提到具体几号，该值为空。")
                       Integer days,

                       @Nullable
                       @ToolParam( description = "是在几月。如果没有提到具体几月份，该值为空。")
                       Integer months,

                       @Nullable
                       @ToolParam( description = "是在哪一年。如果没有提到具体哪一年，该值为空。")
                       Integer years,

                       @ToolParam(description = "任务内容描述："
                  + "1) 简单提醒任务 - 直接填写提醒内容（如'吃药'）"
                  + "2) 动态获取任务 - 填写操作指令格式：'描述'，如'获取热搜标题'")
          String content
  ) {
    String userInput = (String) toolContext.getContext().get("userInput");


    log.info("[setTimingTask] userInput: {}, url: {}, cron: {}, " +
                    "afterSeconds: {}, afterMinutes: {}, afterHours: {}, afterDays: {}, afterMonths: {}, afterYears: {}," +
                    "content: {}",
            userInput, url, cron, afterSeconds, afterMinutes, afterHours, afterDays, afterMonths, afterYears, content);
    try {
      if (afterSeconds != null && afterSeconds < 0) {
      }
      aiService.setUpTimingTask(userInput, url, cron, content);
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