package com.webmonitor.config;

import cn.hutool.json.JSONUtil;
import com.webmonitor.config.fetcher.*;
import com.webmonitor.config.observer.*;
import com.webmonitor.constant.AIModelEnum;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.image.ImageModel;
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
  @Qualifier("zhiPuAiChatModelGlm4Flash")
  private ChatModel zhiPuAiChatModelGlm4Flash;
  @Resource
  @Qualifier("zhiPuAiChatModelGlm45Flash")
  private ChatModel zhiPuAiChatModelGlm45Flash;
  @Resource
  @Qualifier("zhiPuAiChatModelGlm4Plus")
  private ChatModel zhiPuAiChatModelGlm4Plus;
  @Resource
  @Qualifier("zhiPuAiChatModelGlm4Air")
  private ChatModel zhiPuAiChatModelGlm4Air;
  @Resource
  @Qualifier("zhiPuAiChatModelGlm4FlashX")
  private ChatModel zhiPuAiChatModelGlm4FlashX;
  @Resource
  @Qualifier("zhiPuAiChatModelGlmZ1Flash")
  private ChatModel zhiPuAiChatModelGlmZ1Flash;
  @Resource
  @Qualifier("zhiPuAiChatModelGlm4VFlash")
  private ChatModel zhiPuAiChatModelGlm4VFlash;
  @Resource
  @Qualifier("zhiPuAiChatModelGlm41VThinkingFlash")
  private ChatModel zhiPuAiChatModelGlm41VThinkingFlash;
  @Resource
  @Qualifier("zhiPuAiChatModelCogview3Flash")
  private ImageModel zhiPuAiChatModelCogview3Flash;

//  @Resource
//  @Qualifier("deepSeekChatModel")
  private ChatModel deepSeekChatModel;
  @Resource
  @Qualifier("kimiChatModel")
  private ChatModel kimiChatModel;
//  @Resource
//  @Qualifier("customChatModel")
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
          case "AIMcpFetcher":
            configs.add(JSONUtil.toBean(JSONUtil.parseObj(configMap), AIMcpFetcherConfig.class));
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
          case "DBObserver":
            observers.add(JSONUtil.toBean(JSONUtil.parseObj(observerMap), DBObserverConfig.class));
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
    chatModels.put(AIModelEnum.ZHIPU_GLM4_FLASH, zhiPuAiChatModelGlm4Flash);
    chatModels.put(AIModelEnum.ZHIPU_GLM45_FLASH, zhiPuAiChatModelGlm45Flash);
    chatModels.put(AIModelEnum.ZHIPU_GLM4_PLUS, zhiPuAiChatModelGlm4Plus);
    chatModels.put(AIModelEnum.ZHIPU_GLM4_AIR, zhiPuAiChatModelGlm4Air);
    chatModels.put(AIModelEnum.ZHIPU_GLM4_FLASH_X, zhiPuAiChatModelGlm4FlashX);
    chatModels.put(AIModelEnum.ZHIPU_GLMZ1_FLASH, zhiPuAiChatModelGlmZ1Flash);
    chatModels.put(AIModelEnum.ZHIPU_GLM4V_FLASH, zhiPuAiChatModelGlm4VFlash);
    chatModels.put(AIModelEnum.ZHIPU_GLM41V_THINKING_FLASH, zhiPuAiChatModelGlm41VThinkingFlash);
    chatModels.put(AIModelEnum.KIMI, kimiChatModel);
    chatModels.put(AIModelEnum.DEEPSEEK, deepSeekChatModel);
    chatModels.put(AIModelEnum.CUSTOM, customChatModel);
//    chatModels.put(AIModelEnum.GEMINI, vertexAiGeminiChat);
    return chatModels;
  }


  public Map<AIModelEnum, ImageModel> loadAIImageModels() {
    Map<AIModelEnum, ImageModel> imageModelHashMap = new HashMap<>();
    imageModelHashMap.put(AIModelEnum.ZHIPU_COGVIEW3_FLASH, zhiPuAiChatModelCogview3Flash);
    return imageModelHashMap;
  }













}