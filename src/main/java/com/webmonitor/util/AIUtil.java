package com.webmonitor.util;

import com.webmonitor.constant.AIModelEnum;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;

import java.util.Map;

public class AIUtil {

  public static String callAI(String modelName, Map<AIModelEnum, ChatModel> aiModelMap, String prompt) throws Exception {
    if (AIModelEnum.ZHIPU.getName().equals(modelName) && aiModelMap.get(AIModelEnum.ZHIPU) != null) {
      return aiModelMap.get(AIModelEnum.ZHIPU).call(new Prompt(prompt)).getResult().getOutput().getText();
    } else if (AIModelEnum.KIMI.getName().equals(modelName) && aiModelMap.get(AIModelEnum.KIMI) != null) {
      return aiModelMap.get(AIModelEnum.KIMI).call(new Prompt(prompt)).getResult().getOutput().getText();
    } else if (AIModelEnum.CUSTOM.getName().equals(modelName) && aiModelMap.get(AIModelEnum.CUSTOM) != null) {
      return aiModelMap.get(AIModelEnum.CUSTOM).call(new Prompt(prompt)).getResult().getOutput().getText();
    } else {
      throw new Exception("不支持的模型：" + modelName);
    }
  }

}
