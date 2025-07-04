package com.webmonitor.config.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.execution.ToolExecutionException;
import org.springframework.ai.tool.execution.ToolExecutionExceptionProcessor;

@Slf4j
public class CustomToolExecutionExceptionProcessor implements ToolExecutionExceptionProcessor {

  @Override
  public String process(ToolExecutionException exception) {
    // 直接抛出异常
    log.error("Tool execution failed: " + exception.getMessage(), exception);
    throw new RuntimeException("Tool execution failed");
  }


}