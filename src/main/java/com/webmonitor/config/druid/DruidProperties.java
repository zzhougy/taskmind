package com.webmonitor.config.druid;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @description 数据源相关配置
 */
@Configuration
public class DruidProperties {
  @Bean
  @ConfigurationProperties("spring.datasource.druid")
  public DataSource dataSourceOne() {
    return DruidDataSourceBuilder.create().build();
  }
}
