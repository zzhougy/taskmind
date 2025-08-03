package com.webmonitor.util;

import com.webmonitor.constant.AIModelEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClientAttributes;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.core.convert.support.DefaultConversionService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
public class AIUtil {

  public static final String KEYWORD_PROMPT_TXT = "prompts/keyword_generator_prompt.txt";

  public static String callAI(String modelName, Map<AIModelEnum, ChatModel> aiModelMap, String prompt) throws Exception {
    log.info("[callAI] modelName: {}, prompt: {}", modelName, prompt);
    if (AIModelEnum.ZHIPU_GLM4_FLASH.getName().equals(modelName) && aiModelMap.get(AIModelEnum.ZHIPU_GLM4_FLASH) != null) {
      return aiModelMap.get(AIModelEnum.ZHIPU_GLM4_FLASH).call(new Prompt(prompt)).getResult().getOutput().getText();
    } else if (AIModelEnum.ZHIPU_GLM45_FLASH.getName().equals(modelName) && aiModelMap.get(AIModelEnum.ZHIPU_GLM45_FLASH) != null) {
      return aiModelMap.get(AIModelEnum.ZHIPU_GLM45_FLASH).call(new Prompt(prompt)).getResult().getOutput().getText();
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



  public static List<String> getKeywordsFromAI(String cleanedHtml, String modelName, String userQuery,
                                            Map<AIModelEnum, ChatModel> aiModelMap) throws Exception {
    String prompt = getPrompt(userQuery, KEYWORD_PROMPT_TXT, cleanedHtml);
    String aiResult = callAI(modelName, aiModelMap, prompt);
    log.info("ai返回的关键词：{}", aiResult);
    aiResult = aiResult.replace("`",   "");
    aiResult = aiResult.replace("xpath",   "");
    // 去掉换行
    aiResult = aiResult.replace("\n", "");
    log.info("处理通过ai获取关键词后: {}", aiResult);
    return Arrays.stream(aiResult.split("\\|")).toList();
  }

  public static List<String> getKeywordsFromAIByOutputConverter(String cleanedHtml, String modelName, String userQuery,
                                             Map<AIModelEnum, ChatModel> aiModelMap) throws Exception {
    String prompt = getPrompt(userQuery, KEYWORD_PROMPT_TXT, cleanedHtml);
    ChatModel chatModel = aiModelMap.get(AIModelEnum.getByName(modelName));
    ChatClient.Builder builder = ChatClient.builder(chatModel);
    var chatClient = builder.build();
    ListOutputConverter listConverter = new ListOutputConverter(new DefaultConversionService());
    return chatClient.prompt(prompt)
            .advisors(
                    a -> a.param(ChatClientAttributes.OUTPUT_FORMAT.getKey(), listConverter.getFormat())
            ).call().entity(listConverter);
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


  public static String getPrompt(String userQuery, String promptTxtPath) throws IOException {
    // 读取prompt模板
    InputStream inputStream = JsoupUtil.class.getClassLoader()
            .getResourceAsStream(promptTxtPath);
    if (inputStream == null) {
      throw new IOException("Prompt file not found");
    }
    String promptTemplate = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    return promptTemplate + " " + userQuery;
  }












}
