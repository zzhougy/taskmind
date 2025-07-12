package com.webmonitor.controller;

import com.webmonitor.config.WebMonitorFactory;
import com.webmonitor.config.annotation.GuestAccess;
import com.webmonitor.constant.AIModelEnum;
import com.webmonitor.core.WebMonitor;
import com.webmonitor.entity.ResponseVO;
import com.webmonitor.entity.bo.AIUserInputBO;
import com.webmonitor.service.springai.AITools;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/ai")
public class AIController {

  @Resource
  private WebMonitorFactory webMonitorFactory;
  @Resource
  private WebMonitor monitor;
  @Resource
  private AITools aiTools;

  @GuestAccess // todo remove
  @PostMapping("/chat")
  public ResponseVO<String> chatWithAI(@RequestBody AIUserInputBO bo) {
    ChatClient.CallResponseSpec call = ChatClient.create(webMonitorFactory.loadAIModels().get(AIModelEnum.ZHIPU))
            .prompt(bo.getUserInput())
            .tools(aiTools)
            .toolContext(Map.of("userInput", bo.getUserInput()))
            .call();

    String content = call.content();
    log.info("AI Response: {}", content);
    return ResponseVO.success( content);
  }
}