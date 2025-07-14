package com.webmonitor.config;

import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类，敏感词过滤器
 * https://github.com/houbb/sensitive-word
 */
@Configuration
public class SensitiveWordConfig {

  @Bean
  public SensitiveWordBs sensitiveWordBs() {
    return SensitiveWordBs.newInstance().init();
  }
}
