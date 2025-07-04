package com.webmonitor.config.exception;

import org.springframework.ai.tool.execution.ToolExecutionExceptionProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ToolExecutionConfig {
  @Bean
  public ToolExecutionExceptionProcessor toolExecutionExceptionProcessor() {
    return new CustomToolExecutionExceptionProcessor();
  }

}