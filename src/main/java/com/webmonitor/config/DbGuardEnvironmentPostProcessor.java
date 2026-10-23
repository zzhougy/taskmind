package com.webmonitor.config;

import cn.hutool.core.convert.Convert;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;

import java.util.List;
import java.util.Map;

/**
 * DBObserver未启用时，关闭启动期所有DB操作（Druid连接池已通过LazyDataSourceConfig改为懒加载），
 * 这里再关闭auto-table自动建表，保证MySQL未启动时项目也能正常启动。
 */
public class DbGuardEnvironmentPostProcessor implements EnvironmentPostProcessor {

  @Override
  public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
    if (isDbObserverEnabled(environment)) {
      return;
    }
    Map<String, Object> props = Map.of("auto-table.enable", false);
    environment.getPropertySources().addFirst(new MapPropertySource("webMonitorDbGuard", props));
  }

  /**
   * 判断DBObserver是否启用（enabled支持 0/1、true/false 等写法）
   */
  public static boolean isDbObserverEnabled(Environment environment) {
    try {
      List<Map> observers = Binder.get(environment)
              .bind("web-monitor.observers", Bindable.listOf(Map.class))
              .orElse(null);
      if (observers == null) {
        return false;
      }
      for (Map<String, Object> observer : observers) {
        if ("DBObserver".equals(observer.get("type"))) {
          return Convert.toBool(observer.get("enabled"), false);
        }
      }
    } catch (Exception e) {
      System.err.println("判断DBObserver是否启用失败: " + e.getMessage());
    }
    return false;
  }
}
