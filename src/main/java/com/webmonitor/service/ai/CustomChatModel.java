package com.webmonitor.service.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Slf4j
@Service("customChatModel")
public class CustomChatModel implements ChatModel {
  @Override
  public ChatResponse call(Prompt prompt) {
    log.info("CustomAI call: {}", prompt);
    return new ChatResponse( new ArrayList<>());
  }

  @Override
  public String call(String message) {
    return ChatModel.super.call(message);
  }
}
