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


    // 测试用例: {频率, 小时, 分钟, 月份, 日期, 间隔, 预期表达式}
    Object[][] testCases = {
            // 分钟级测试
            {"minutely", 0, 0, null, null, 1, "*/1 * * * * ?"},
            {"minutely", 0, 0, null, null, 30, "*/30 * * * * ?"},
            {"minutely", 0, 0, null, null, 59, "*/59 * * * * ?"},

            // 小时级测试
            {"hourly", 0, 0, null, null, 1, "0 */1 * * * ?"},
            {"hourly", 0, 0, null, null, 6, "0 */6 * * * ?"},
            {"hourly", 0, 0, null, null, 23, "0 */23 * * * ?"},

            // 天级测试
            {"daily", 8, 30, null, null, null, "0 30 8 * * ?"},
            {"daily", 12, 0, null, null, null, "0 0 12 * * ?"},
            {"daily", 23, 59, null, null, null, "0 59 23 * * ?"},

            // 周级测试 (实际星期几会根据当前日期变化，这里只验证格式)
            {"weekly", 9, 15, null, null, null, null},

            // 月级测试
            {"monthly", 10, 0, null, 1, null, "0 0 10 1 * ?"},
            {"monthly", 14, 30, null, 15, null, "0 30 14 15 * ?"},
            {"monthly", 20, 45, null, 31, null, "0 45 20 31 * ?"},

            // 年级测试
            {"yearly", 0, 0, 1, 1, null, "0 0 0 1 1 ?"},
            {"yearly", 12, 30, 6, 18, null, "0 30 12 18 6 ?"},
            {"yearly", 18, 0, 12, 25, null, "0 0 18 25 12 ?"},
            // 边界值测试
            {"minutely", 0, 0, null, null, 0, null},    // 无效间隔(0)
            {"minutely", 0, 0, null, null, 60, null},   // 无效间隔(60)
            {"hourly", 0, 0, null, null, 0, null},      // 无效间隔(0)
            {"hourly", 0, 0, null, null, 24, null},     // 无效间隔(24)
            {"daily", 24, 30, null, null, null, null},  // 无效小时(24)
            {"daily", -1, 30, null, null, null, null},  // 无效小时(-1)
            {"daily", 12, 60, null, null, null, null},  // 无效分钟(60)
            {"daily", 12, -1, null, null, null, null},  // 无效分钟(-1)
            {"monthly", 10, 0, null, 0, null, null},    // 无效日期(0)
            {"monthly", 10, 0, null, 32, null, null},   // 无效日期(32)
            {"yearly", 10, 0, 0, 15, null, null},       // 无效月份(0)
            {"yearly", 10, 0, 13, 15, null, null},      // 无效月份(13)
            {"yearly", 10, 0, 2, 30, null, null},       // 无效日期(2月30日)
            {"invalid", 12, 30, null, null, null, null} // 无效频率类型
    };

    System.out.println("=== CronUtil 测试用例 ===");
    for (Object[] testCase : testCases) {
      String frequency = (String) testCase[0];
      int hour = (Integer) testCase[1];
      int minute = (Integer) testCase[2];
      Integer month = (Integer) testCase[3];
      Integer day = (Integer) testCase[4];
      Integer interval = (Integer) testCase[5];
      String expected = (String) testCase[6];

      try {
        String cron = generateCronExpression(frequency, null, hour, minute, month, day, interval, null, null);
        String status = (expected == null || cron.matches(expected.replace("*", ".*"))) ? "PASS" : "FAIL";
        System.out.printf("%-7s | %-7s | H:%02d M:%02d | 月:%2s 日:%2s 间隔:%2s | 生成结果: %-20s | 预期: %s%n",
                status, frequency, hour, minute, month, day, interval, cron, expected == null ? "(动态匹配)" : expected);
      } catch (Exception e) {
        System.out.printf("FAIL   | %-7s | H:%02d M:%02d | 月:%2s 日:%2s 间隔:%2s | 错误: %s%n",
                frequency, hour, minute, month, day, interval, e.getMessage());
      }
    }


  }


  /**
   * 根据频率、小时和分钟生成cron表达式
   *
   * @param frequency 频率类型: daily, weekly, monthly
   * @param hour      小时 (0-23)
   * @param minute    分钟 (0-59)
   * @return cron表达式或null
   */
  public static String generateCronExpression(String frequency, Integer second, Integer hour, Integer minute, Integer month, Integer day, Integer interval, Integer dayOfWeek, Integer year) {

    // todo 验证参数
    //    if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
//      return null;
//    }

    switch (frequency) {
      case "perSecond":
        return String.format("*/%d * * * * ?", second);
      case "minutely":
        return String.format("0 */%d * * * ?", interval);
      case "hourly":
        return String.format("0 0 */%d * * ?", interval);
      case "daily":
        return String.format("0 %d %d * * ?", minute, hour);
      case "weekly":
        // 获取当前星期几 (1=周日, 2=周一, ..., 7=周六 in Quartz)
        // Java的DayOfWeek.getValue()返回1=周一, 7=周日，需要转换为Quartz格式
//        int javaDayOfWeek = LocalDate.now().getDayOfWeek().getValue();
//        int quartzDayOfWeek = javaDayOfWeek == 7 ? 1 : javaDayOfWeek + 1;
        return String.format("0 %d %d ? * %d", minute == null ? 0 : minute, hour, dayOfWeek);
      case "monthly":
        // 获取当前日期
        return String.format("0 %d %d %d * ?", minute == null ? 0 : minute, hour, day);
      case "yearly":
        return String.format("0 %d %d %d %d ?", minute, hour, day, month);
      case "once":
        return String.format("%d %d %d %d %d ? %d", second == null ? 0 : second, minute == null ? 0 : minute, hour, day, month, year);
      default:
        return null;
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
    description = description.replace(" 月 ", " ");
    if (description.contains("星期")) {
      description = description.replace(" 天", "");
    } else {
      description = description.replace("天", " 号 ");
    }

    // 输出结果
    log.info("Cron表达式: " + cronExpression);
    log.info("中文描述: " + description);
    return description;
  }
}
