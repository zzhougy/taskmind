package com.webmonitor.util;

import com.cronutils.descriptor.CronDescriptor;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.Optional;

@Slf4j
public class CronUtil {


  public static long getIntervalInSeconds(String cronExpression) {
    // 解析cron表达式
    CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);
    CronParser parser = new CronParser(cronDefinition);
    Cron cron = parser.parse(cronExpression);
    cron.validate();

    ExecutionTime executionTime = ExecutionTime.forCron(cron);
    ZonedDateTime now = ZonedDateTime.now();

    // 获取下一次执行时间
    Optional<ZonedDateTime> nextOpt = executionTime.nextExecution(now);
    if (!nextOpt.isPresent()) {
      return 0; // 无后续执行，视为单次执行
    }
    ZonedDateTime next = nextOpt.get();

    // 获取下下次执行时间
    Optional<ZonedDateTime> nextNextOpt = executionTime.nextExecution(next);
    if (!nextNextOpt.isPresent()) {
      return 0; // 仅有一次后续执行
    }
    ZonedDateTime nextNext = nextNextOpt.get();


    // 计算间隔（秒）
    long l = nextNext.toEpochSecond() - next.toEpochSecond();

    log.info("Cron: {} Interval: {} seconds", cronExpression, l);

    return nextNext.toEpochSecond() - next.toEpochSecond();



  }

  public static void main(String[] args) {
    String[] examples = {
            "0 0 12 * * ?",        // 每天中午12点
            "0 15 10 * * *",       // 每天上午10:15
            "0 0 12 1/5 * *",      // 每月第1天及之后每5天的中午12点
            "0 0/5 14 * * *",      // 每天下午2点开始每5分钟
            "0 15 10 * * MON-FRI", // 周一至周五上午10:15
            "0 0 12 1 1 *",         // 每年1月1日中午12点
            "*/30 * * * * ?",
            "0 20 11 12 7 ? 2025"
    };

    for (String cron : examples) {
      try {
        System.out.printf("Cron: %-20s Interval: %d seconds%n", cron, getIntervalInSeconds(cron));
      } catch (Exception e) {
        System.out.printf("Cron: %-20s Error: %s%n", cron, e.getMessage());
      }
    }
  }

  public static String convertDescription(String cronExpression) {

    // 创建Cron定义
    CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);

    // 创建Cron解析器
    CronParser parser = new CronParser(cronDefinition);

    // 解析Cron表达式
    com.cronutils.model.Cron cron = parser.parse(cronExpression);

    // 创建Cron描述器，指定语言为中文
    CronDescriptor descriptor = CronDescriptor.instance(Locale.CHINA);

    // 获取中文描述
    String description = descriptor.describe(cron);

    // 输出结果
    log.info("Cron表达式: " + cronExpression);
    log.info("中文描述: " + description);
    return description;
  }
}
