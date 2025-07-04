package com.webmonitor.controller;

import com.webmonitor.config.WebMonitorFactory;
import com.webmonitor.constant.AIModelEnum;
import com.webmonitor.core.WebMonitor;
import com.webmonitor.entity.bo.AIUserInputBO;
import com.webmonitor.service.ai.AITools;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController("/api/ai")
public class WebMonitorController {

  @Resource
  private WebMonitorFactory webMonitorFactory;
  @Resource
  private WebMonitor monitor;
  @Resource
  private AITools aiTools;

  @PostMapping("/chat")
  public String chatWithAI(@RequestBody AIUserInputBO bo) {
    ChatClient.CallResponseSpec call = ChatClient.create(webMonitorFactory.loadAIModels().get(AIModelEnum.ZHIPU))
            .prompt(bo.getUserInput())
            .tools(aiTools)
            .call();

//    String response = ChatClient.create(webMonitorFactory.loadAIModels().get(AIModelEnum.ZHIPU))
//            .prompt(bo.getUserInput())
//            .tools(aiTools)
//            .call()
//            .content();

    String content = call.content();
    log.info("AI Response: {}", content);
    return content;
  }
}