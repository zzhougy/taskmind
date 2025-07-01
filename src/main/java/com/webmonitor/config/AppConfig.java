//package com.webmonitor.config;
//
//import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
//import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
//import org.springframework.ai.zhipuai.api.ZhiPuAiApi;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//
//@Configuration
//public class AppConfig {
//
//  @Primary
//  @Bean(name = "ZhipuAiChatModel")
//  public ZhiPuAiChatModel chatClient() {
//    var zhiPuAiApi = new ZhiPuAiApi(System.getenv("ZHIPU_AI_API_KEY"));
//    var chatModel = new ZhiPuAiChatModel(zhiPuAiApi, ZhiPuAiChatOptions.builder()
//            .model(ZhiPuAiApi.ChatModel.GLM_3_Turbo.getValue())
//            .temperature(0.4)
//            .maxTokens(200)
//            .build());
//
//    return chatModel;
//  }
//
//
//
//
//
//
//}