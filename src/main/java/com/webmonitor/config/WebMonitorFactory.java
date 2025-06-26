package com.webmonitor.config;

import cn.hutool.json.JSONUtil;
import com.webmonitor.config.fetcher.*;
import com.webmonitor.config.observer.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class WebMonitorFactory {

  @Autowired
  private WebMonitorProperties webMonitorProperties;


  public List<FetcherConfig> loadFetcherConfigs() {
    List<FetcherConfig> configs = new ArrayList<>();
    for (Map<String, Object> configMap : webMonitorProperties.getConfigs()) {
      try {
        switch ((String) configMap.get("type")) {
          case "ZzFetcher":
            configs.add(JSONUtil.toBean(JSONUtil.parseObj(configMap), ZzFetcherConfig.class));
            break;
          case "CssSelectorFetcher":
            configs.add(JSONUtil.toBean(JSONUtil.parseObj(configMap), CssSelectorFetcherConfig.class));
            break;
          case "XPathFetcher":
            configs.add(JSONUtil.toBean(JSONUtil.parseObj(configMap), XPathFetcherConfig.class));
            break;
          case "SeleniumFetcher":
            configs.add(JSONUtil.toBean(JSONUtil.parseObj(configMap), SeleniumFetcherConfig.class));
            break;
          default:
            throw new IllegalArgumentException("Unknown fetcher type: " + configMap.get("type"));
        }
      } catch (Exception e) {
        throw new RuntimeException("Failed to bind config", e);
      }
    }
    return configs;
  }

  public List<ObserverConfig> loadObserverConfigs() {
    List<ObserverConfig> observers = new ArrayList<>();
    for (Map<String, Object> observerMap : webMonitorProperties.getObservers()) {
      try {
        switch ((String) observerMap.get("type")) {
          case "ConsoleObserver":
            observers.add(JSONUtil.toBean(JSONUtil.parseObj(observerMap), ConsoleObserverConfig.class));
            break;
          case "EmailObserver":
            observers.add(JSONUtil.toBean(JSONUtil.parseObj(observerMap), EmailObserverConfig.class));
            break;
          case "QyWeixinObserver":
            observers.add(JSONUtil.toBean(JSONUtil.parseObj(observerMap), QyWeixinObserverConfig.class));
            break;
          case "SlackObserver":
            observers.add(JSONUtil.toBean(JSONUtil.parseObj(observerMap), SlackObserverConfig.class));
            break;
          default:
            throw new IllegalArgumentException("Unknown observer type: " + observerMap.get("type"));
        }
      } catch (Exception e) {
        throw new RuntimeException("Failed to bind config", e);
      }
    }
    return observers;
  }
}