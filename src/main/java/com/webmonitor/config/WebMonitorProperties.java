package com.webmonitor.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "web-monitor")
@EnableConfigurationProperties
@Data
public class WebMonitorProperties {

  private List<Map<String, Object>> configs;
  private List<Map<String, Object>> observers;

}
