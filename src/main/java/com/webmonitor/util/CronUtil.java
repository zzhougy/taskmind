package com.webmonitor.util;

import com.cronutils.descriptor.CronDescriptor;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.parser.CronParser;
import lombok.extern.slf4j.Slf4j;

import java.util.Locale;

@Slf4j
public class CronUtil {

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
