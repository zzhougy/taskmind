package com.webmonitor.util;

import com.webmonitor.constant.AIModelEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
public class AIUtil {

  public static final String KEYWORD_PROMPT_TXT = "prompts/keyword_generator_prompt.txt";

  public static String callAI(String modelName, Map<AIModelEnum, ChatModel> aiModelMap, String prompt) throws Exception {
    log.info("[callAI] modelName: {}, prompt: {}", modelName, prompt);
    if (AIModelEnum.ZHIPU.getName().equals(modelName) && aiModelMap.get(AIModelEnum.ZHIPU) != null) {
      return aiModelMap.get(AIModelEnum.ZHIPU).call(new Prompt(prompt)).getResult().getOutput().getText();
    } else if (AIModelEnum.KIMI.getName().equals(modelName) && aiModelMap.get(AIModelEnum.KIMI) != null) {
      return aiModelMap.get(AIModelEnum.KIMI).call(new Prompt(prompt)).getResult().getOutput().getText();
    } else if (AIModelEnum.CUSTOM.getName().equals(modelName) && aiModelMap.get(AIModelEnum.CUSTOM) != null) {
      return aiModelMap.get(AIModelEnum.CUSTOM).call(new Prompt(prompt)).getResult().getOutput().getText();
    } else if (AIModelEnum.DEEPSEEK.getName().equals(modelName) && aiModelMap.get(AIModelEnum.DEEPSEEK) != null) {
      return aiModelMap.get(AIModelEnum.DEEPSEEK).call(new Prompt(prompt)).getResult().getOutput().getText();
    } else if (AIModelEnum.GEMINI.getName().equals(modelName) && aiModelMap.get(AIModelEnum.GEMINI) != null) {
      return aiModelMap.get(AIModelEnum.GEMINI).call(new Prompt(prompt)).getResult().getOutput().getText();
    } else {
      throw new Exception("不支持的模型：" + modelName);
    }
  }



  public static String getKeywordFromAI(String cleanedHtml, String modelName, String userQuery,
                                            Map<AIModelEnum, ChatModel> aiModelMap) throws Exception {
    String prompt = getPrompt(userQuery, KEYWORD_PROMPT_TXT, cleanedHtml);
    return callAI(modelName, aiModelMap, prompt);
  }

  public static String getPrompt(String userQuery, String promptTxtPath , String cleanedHtml) throws IOException {
    // 读取prompt模板
    InputStream inputStream = JsoupUtil.class.getClassLoader()
            .getResourceAsStream(promptTxtPath);
    if (inputStream == null) {
      throw new IOException("Prompt file not found");
    }
    String promptTemplate = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    String prompt = promptTemplate.replace("用户本次需求：", "用户本次需求：" + userQuery)
            .replace("HTML 内容如下：", "HTML 内容如下：" + cleanedHtml);
    return prompt;
  }













}
