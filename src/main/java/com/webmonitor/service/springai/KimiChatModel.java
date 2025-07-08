package com.webmonitor.service.springai;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.webmonitor.entity.Message;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service("kimiChatModel")
public class KimiChatModel implements ChatModel {


  @Value("${spring.ai.kimi.api-key}")
  private String kimiApiKey;

  @Value("${spring.ai.kimi.base-url}")
  private String baseUrl;

  @Value("${spring.ai.kimi.chat.options.model}")
  private String modelName;

  @Override
  public ChatResponse call(Prompt prompt) {
    log.info("kimiChatModel call: {}", prompt);

    String contents = prompt.getContents();
    String response = null;
    try {
      response = chat(List.of(Message.builder().role("user")
              .content(contents).build()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    log.info("kimiChatModel response: {}", response);

    Generation generation = new Generation(new AssistantMessage("response"));

    return new ChatResponse(List.of(generation));
  }

  @Override
  public String call(String message) {
    return ChatModel.super.call(message);
  }


  public String chat(@NonNull List<Message> messages) throws IOException {
    String requestBody = new JSONObject()
            .putOpt("model", modelName)
            .putOpt("messages", messages)
            .putOpt("stream", true)
            .toString();
    Request okhttpRequest = new Request.Builder()
            .url(baseUrl)
            .post(RequestBody.create(requestBody, MediaType.get(ContentType.JSON.getValue())))
            .addHeader("Authorization", "Bearer " + kimiApiKey)
            .build();
    Call call = new OkHttpClient().newCall(okhttpRequest);
    Response okhttpResponse = call.execute();
    BufferedReader reader = new BufferedReader(okhttpResponse.body().charStream());
    StringBuilder stringBuilder = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) {
      if (StrUtil.isBlank(line)) {
        continue;
      }
      if (JSONUtil.isTypeJSON(line)) {
        Optional.of(JSONUtil.parseObj(line))
                .map(x -> x.getJSONObject("error"))
                .map(x -> x.getStr("message"))
                .ifPresent(x -> System.out.println("error: " + x));
        return stringBuilder.toString();

      }
      line = StrUtil.replace(line, "data: ", StrUtil.EMPTY);
      if (StrUtil.equals("[DONE]", line) || !JSONUtil.isTypeJSON(line)) {
        return stringBuilder.toString();

      }
      Optional.of(JSONUtil.parseObj(line))
              .map(x -> x.getJSONArray("choices"))
              .filter(CollUtil::isNotEmpty)
              .map(x -> (JSONObject) x.get(0))
              .map(x -> x.getJSONObject("delta"))
              .map(x -> x.getStr("content"))
              .ifPresent(x -> {
                System.out.println("rowData: " + x);

                stringBuilder.append(x);
              });
    }
    return stringBuilder.toString();
  }
}
