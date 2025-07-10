package com.webmonitor.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Collections;

/**
 * @description 配置分页
 */
@EnableTransactionManagement
@Configuration
@MapperScan("com.webmonitor.mapper.*Mapper")
public class MybatisPlusConfig {
  /**
   * 分页插件 3.5.X
   */
  @Bean
  public PaginationInnerInterceptor paginationInnerInterceptor() {
    PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor();
    paginationInterceptor.setMaxLimit(100L);
    paginationInterceptor.setDbType(DbType.MYSQL);
    paginationInterceptor.setOptimizeJoin(true);
    return paginationInterceptor;
  }
  @Bean
  public MybatisPlusInterceptor mybatisPlusInterceptor(){
    MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
    mybatisPlusInterceptor.setInterceptors(Collections.singletonList(paginationInnerInterceptor()));
    return mybatisPlusInterceptor;
  }

}