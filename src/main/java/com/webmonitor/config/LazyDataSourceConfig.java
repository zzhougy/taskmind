package com.webmonitor.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * 自定义DataSource，替代druid starter自带的 @Bean(initMethod = "init")（启动时立即建立连接池）。
 * 这里不指定initMethod，Druid会在首次getConnection()时才初始化连接池，
 * 这样MySQL未启动时项目也能正常启动。
 */
@Configuration
public class LazyDataSourceConfig {

  @Bean
  @Primary
  @ConfigurationProperties("spring.datasource.druid")
  public DataSource dataSource() {
    return DruidDataSourceBuilder.create().build();
  }
}
