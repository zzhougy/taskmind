package com.webmonitor.controller;

import com.webmonitor.config.annotation.GuestAccess;
import com.webmonitor.entity.ResponseVO;
import com.webmonitor.entity.bo.AIUserInputBO;
import com.webmonitor.service.AIService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/ai")
public class AIController {

  @Resource
  private AIService aiService;


  @GuestAccess // todo remove
  @PostMapping("/chat")
  public ResponseVO<String> chatWithAI(@RequestBody AIUserInputBO bo) {
    return ResponseVO.success( aiService.chatWithAIEntrance(bo));
  }
}