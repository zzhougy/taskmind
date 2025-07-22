package com.webmonitor.service.springai;

import com.webmonitor.config.exception.BusinessException;
import com.webmonitor.constant.FrequencyEnum;
import com.webmonitor.service.AIService;
import com.webmonitor.util.CronUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.stream.Stream;


@Slf4j
@Component
public class TaskTools {

  public static final String TASK_SETTING_SUCCESS = "设置成功";
  public static final String TASK_SETTING_SUCCESS2 = "\"设置成功\"";
  public static final String TASK_SETTING_ERROR = "失败请修改描述重试或者联系管理员";
  @Resource
  @Lazy
  private AIService aiService;


  @Tool(name = "获取当前时间", description = "获取当前时间")
  String getCurrentTime() {
    log.info("====[getCurrentTime]");
    return LocalDateTime.now().toString();
  }

  @Tool(name = "设置定时提醒或者执行任务。重点注意: 1) 周期性任务一定不是ONCE 2) 绝对时间一次性任务/相对时间一次性任务一定是ONCE",
          description = "支持3种模式：1) 周期性任务 2) 绝对时间一次性任务 3) 相对时间一次性任务（如'X分钟后'），注意frequency一定是ONCE",
          returnDirect = true)
  String setTimingTask(ToolContext toolContext,

          @ToolParam(description = "任务URL（如有）", required = false)
          String url,

                       @ToolParam(description = "任务执行频率可选值:ONCE|PERSECOND|MINUTELY|HOURLY|DAILY|WEEKLY|MONTHLY|YEARLY。" +
                               "重点注意: 1) 周期性任务一定不是ONCE 2) 绝对时间一次性任务/相对时间一次性任务一定是ONCE")
                       FrequencyEnum frequency,

                       @ToolParam(description = "年，once 频率时必须", required = false)
                       Integer year,

                       @ToolParam(description = "秒(1-59)，once/perSecond 频率时必须", required = false)
                       Integer second,

                       @ToolParam(description = "小时(0-23)，once/daily/weekly/monthly/yearly 频率时必须", required = false)
                       Integer hour,

                       @ToolParam(description = "分钟(0-59)，once/daily/weekly/monthly/yearly 频率时必须", required = false)
                       Integer minute,

                       @ToolParam(description = "日期(1-31)，once/monthly/yearly 频率时必须", required = false)
                       Integer day,

                       @ToolParam(description = "月份(1-12)，once/yearly 频率时必须", required = false)
                       Integer month,

                       @ToolParam(description = "星期几(1-7)，weekly 频率时必须", required = false)
                       Integer dayOfWeek,

                       @ToolParam(description = "重点注意: minutely(1-59) 或者 perSecond(1-59) 或者 hourly(1-23) 频率时必填", required = false)
                       Integer interval,

                       @ToolParam(description = "x秒后", required = false)
                       Integer afterSeconds,

                       @ToolParam(description = "x分钟后", required = false)
                       Integer afterMinutes,

                       @ToolParam(description = "x小时后", required = false)
                       Integer afterHours,

                       @ToolParam(description = "x天后", required = false)
                       Integer afterDays,

                       @ToolParam(description = "任务内容描述："
                  + "1) 简单提醒任务 - 直接填写提醒内容（如'吃药'）"
                  + "2) 动态获取任务 - 填写操作指令格式：'描述'，如'获取热搜标题'")
          String content
  ) {
    String userInput = (String) toolContext.getContext().get("userInput");

    log.info("setTimingTask invoked: userInput={}, " +
                    "url={}, frequency={}, year={}, second={}, hour={}, minute={}, day={}, month={}, " +
                    "dayOfWeek={}, interval={}, afterSeconds={}, afterMinutes={}, afterHours={}, afterDays={}, content={}",
            userInput, url, frequency, year, second, hour, minute, day, month,
            dayOfWeek, interval, afterSeconds, afterMinutes, afterHours, afterDays, content);
    try {


      /* ----------------- 相对时间转绝对时间 ----------------- */
      if (FrequencyEnum.ONCE == frequency) {
        // 只有一次性任务才需要相对时间计算
        if (Stream.of(afterSeconds, afterMinutes, afterHours, afterDays)
                .anyMatch(Objects::nonNull)) {

          // 1. 计算总偏移毫秒
          long shiftMs = 0L;
          if (afterSeconds  != null) shiftMs += afterSeconds  * 1_000L;
          if (afterMinutes  != null) shiftMs += afterMinutes  * 60_000L;
          if (afterHours    != null) shiftMs += afterHours    * 3_600_000L;
          if (afterDays     != null) shiftMs += afterDays     * 86_400_000L;

          if (shiftMs <= 0) {
            throw new BusinessException("相对时间必须大于 0");
          }

          // 2. 得到未来的绝对时间
          ZonedDateTime future = ZonedDateTime.now(ZoneId.systemDefault())
                  .plus(Duration.ofMillis(shiftMs));

          // 3. 回填绝对时间字段
          year   = year == null ? future.getYear() : year;
          month  = month == null ? future.getMonthValue() : month;
          day    = day == null ? future.getDayOfMonth() : day;
          // 适配5天后的下午3点
          if (hour != null && minute == null) {
            minute = 0;
          } else {
            minute = future.getMinute();
          }
          if (hour != null && second == null) {
            second = 0;
          } else {
            second = future.getSecond();
          }
          hour = hour == null ? future.getHour() : hour;
        }
      }
      /* ------------------------------------------------------ */

      // 验证perSecond频率的second参数
      if ("perSecond".equals(frequency)) {
        if (second == null || second < 1 || second > 59) {
          throw new BusinessException("perSecond频率时秒参数必须为1-59之间的整数");
        }
      }
      // 验证once频率的参数
      String cron = CronUtil.generateCronExpression(frequency.getCode(), second, hour, minute, month, day, interval, dayOfWeek, year);
      log.info("setTimingTask invoked: userInput={}, " +
                      "url={}, frequency={}, year={}, second={}, hour={}, minute={}, day={}, month={}, " +
                      "dayOfWeek={}, interval={}, afterSeconds={}, afterMinutes={}, afterHours={}, afterDays={}, content={}, " +
                      "cron={}",
              userInput, url, frequency, year, second, hour, minute, day, month,
              dayOfWeek, interval, afterSeconds, afterMinutes, afterHours, afterDays, content, cron);
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