package com.webmonitor.config.ai;

import org.springframework.ai.model.zhipuai.autoconfigure.ZhiPuAiChatProperties;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.ai.zhipuai.ZhiPuAiImageModel;
import org.springframework.ai.zhipuai.ZhiPuAiImageOptions;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;
import org.springframework.ai.zhipuai.api.ZhiPuAiImageApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class ZhipuModelConfig {

  @Bean(name = "zhiPuAiChatModelGlm4Flash")
  public ZhiPuAiChatModel chatClient(ZhiPuAiChatProperties properties) {
      var zhiPuAiApi = new ZhiPuAiApi(properties.getApiKey());
      var chatModel = new ZhiPuAiChatModel(zhiPuAiApi, ZhiPuAiChatOptions.builder()
              .model("GLM-4-Flash-250414")
              .build());
      return chatModel;
  }


  @Bean(name = "zhiPuAiChatModelGlm45Flash")
  public ZhiPuAiChatModel chatClientGlm45Flash(ZhiPuAiChatProperties properties) {
    var zhiPuAiApi = new ZhiPuAiApi(properties.getApiKey());
    var chatModel = new ZhiPuAiChatModel(zhiPuAiApi, ZhiPuAiChatOptions.builder()
            .model("GLM-4.5-Flash")
            .build());
    return chatModel;
  }

  @Bean(name = "zhiPuAiChatModelGlm4Plus")
  public ZhiPuAiChatModel zhiPuAiChatModelGlm4Plus(ZhiPuAiChatProperties properties) {
    var zhiPuAiApi = new ZhiPuAiApi(properties.getApiKey());
    var chatModel = new ZhiPuAiChatModel(zhiPuAiApi, ZhiPuAiChatOptions.builder()
            .model("glm-4-plus")
            .build());
    return chatModel;
  }


  @Bean(name = "zhiPuAiChatModelGlm4Air")
  public ZhiPuAiChatModel zhiPuAiChatModelGlm4Air(ZhiPuAiChatProperties properties) {
    var zhiPuAiApi = new ZhiPuAiApi(properties.getApiKey());
    var chatModel = new ZhiPuAiChatModel(zhiPuAiApi, ZhiPuAiChatOptions.builder()
            .model("GLM-4-Air-250414")
            .build());
    return chatModel;
  }

  @Bean(name = "zhiPuAiChatModelGlm4FlashX")
  public ZhiPuAiChatModel zhiPuAiChatModelGlm4FlashX(ZhiPuAiChatProperties properties) {
    var zhiPuAiApi = new ZhiPuAiApi(properties.getApiKey());
    var chatModel = new ZhiPuAiChatModel(zhiPuAiApi, ZhiPuAiChatOptions.builder()
            .model("GLM-4-FlashX-250414")
            .build());
    return chatModel;
  }



  @Bean(name = "zhiPuAiChatModelGlmZ1Flash")
  public ZhiPuAiChatModel zhiPuAiChatModelGlmZ1Flash(ZhiPuAiChatProperties properties) {
    var zhiPuAiApi = new ZhiPuAiApi(properties.getApiKey());
    var chatModel = new ZhiPuAiChatModel(zhiPuAiApi, ZhiPuAiChatOptions.builder()
            .model("GLM-Z1-Flash")
            .build());
    return chatModel;
  }

  // GLM-4V-Flash 模型配置
  @Bean(name = "zhiPuAiChatModelGlm4VFlash")
  public ZhiPuAiChatModel zhiPuAiChatModelGlm4VFlash(ZhiPuAiChatProperties properties) {
    var zhiPuAiApi = new ZhiPuAiApi(properties.getApiKey());
    var chatModel = new ZhiPuAiChatModel(zhiPuAiApi, ZhiPuAiChatOptions.builder()
            .model("GLM-4V-Flash")
            .build());
    return chatModel;
  }

  // GLM-4.1V-Thinking-Flash 模型配置
  @Bean(name = "zhiPuAiChatModelGlm41VThinkingFlash")
  public ZhiPuAiChatModel zhiPuAiChatModelGlm41VThinkingFlash(ZhiPuAiChatProperties properties) {
    var zhiPuAiApi = new ZhiPuAiApi(properties.getApiKey());
    var chatModel = new ZhiPuAiChatModel(zhiPuAiApi, ZhiPuAiChatOptions.builder()
            .model("GLM-4.1V-Thinking-Flash")
            .build());
    return chatModel;
  }

  // Cogview-3-Flash
  @Bean(name = "zhiPuAiChatModelCogview3Flash")
  public ZhiPuAiImageModel zhiPuAiChatModelCogview3Flash(ZhiPuAiChatProperties properties) {
    var zhiPuAiImageApi = new ZhiPuAiImageApi(properties.getApiKey());
    var aiImageModel = new ZhiPuAiImageModel(zhiPuAiImageApi, ZhiPuAiImageOptions.builder()
            .model("CogView-3-Flash")
            .build(),
            RetryTemplate.builder().build());
    return aiImageModel;
  }

}