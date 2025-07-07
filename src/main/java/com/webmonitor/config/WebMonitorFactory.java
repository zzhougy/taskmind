package com.webmonitor.config;

import cn.hutool.json.JSONUtil;
import com.webmonitor.config.fetcher.*;
import com.webmonitor.config.observer.*;
import com.webmonitor.constant.AIModelEnum;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WebMonitorFactory {

  @Autowired
  private WebMonitorProperties webMonitorProperties;
  @Resource
  @Qualifier("zhiPuAiChatModel")
  private ChatModel zhiPuAiChatModel;
  @Resource
  @Qualifier("deepSeekChatModel")
  private ChatModel deepSeekChatModel;
  @Resource
  @Qualifier("kimiChatModel")
  private ChatModel kimiChatModel;
  @Resource
  @Qualifier("customChatModel")
  private ChatModel customChatModel;
//  @Resource
//  @Qualifier("vertexAiGeminiChat")
//  private ChatModel vertexAiGeminiChat;


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
          case "KeywordSelectorFetcher":
            configs.add(JSONUtil.toBean(JSONUtil.parseObj(configMap), KeywordSelectorFetcherConfig.class));
            break;
          case "AIFetcher":
            configs.add(JSONUtil.toBean(JSONUtil.parseObj(configMap), AIFetcherConfig.class));
            break;
          case "SimpleFetcher":
            configs.add(JSONUtil.toBean(JSONUtil.parseObj(configMap), SimpleFetcherConfig.class));
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



  public Map<AIModelEnum, ChatModel> loadAIModels() {
    Map<AIModelEnum, ChatModel> chatModels = new HashMap<>();
    chatModels.put(AIModelEnum.ZHIPU, zhiPuAiChatModel);
    chatModels.put(AIModelEnum.KIMI, kimiChatModel);
    chatModels.put(AIModelEnum.DEEPSEEK, deepSeekChatModel);
    chatModels.put(AIModelEnum.CUSTOM, customChatModel);
//    chatModels.put(AIModelEnum.GEMINI, vertexAiGeminiChat);
    return chatModels;
  }











}