package com.webmonitor.service.observer;

import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class AbstractMessageObserver implements WebObserver {
  protected static final MediaType JSON = MediaType.get("application/json");
  protected static final String CONFIG_FILE = "application.yml";
  protected static final int MAX_CONTENT_LENGTH = 2000;
  protected static final int MAX_RETRY_TIMES = 1;
  protected static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  protected final OkHttpClient client;
  protected final ObjectMapper objectMapper;
  protected final String webhookUrl;
  protected final boolean mentionAll;

  protected AbstractMessageObserver(String webhookBaseUrlProperty, String webhookKeyProperty, boolean mentionAll) {
    this.client = createHttpClient();
    this.objectMapper = new ObjectMapper();
    this.webhookUrl = loadWebhookUrl(webhookBaseUrlProperty, webhookKeyProperty);
    this.mentionAll = mentionAll;
  }

  protected OkHttpClient createHttpClient() {
    return new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build();
  }

  protected String loadWebhookUrl(String webhookBaseUrlProperty, String webhookKeyProperty) {
    try {
      Properties props = new Properties();
      try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
        if (inputStream == null) {
          throw new IOException("配置文件 " + CONFIG_FILE + " 未找到");
        }
        props.load(inputStream);
      }

      String baseUrl = props.getProperty(webhookBaseUrlProperty);
      String key = null;
      if (!ObjectUtil.isEmpty(webhookKeyProperty)) {
        key = props.getProperty(webhookKeyProperty);
      }

      if (ObjectUtil.isEmpty(baseUrl)) {
        throw new IllegalStateException("Webhook配置不完整");
      }

      return ObjectUtil.isEmpty(key) ? baseUrl : baseUrl + "?key=" + key;
    } catch (IOException e) {
      log.error("加载Webhook配置失败", e);
      throw new RuntimeException("加载Webhook配置失败", e);
    }
  }

  protected String truncateContent(String content) {
    if (content.length() > MAX_CONTENT_LENGTH) {
      return content.substring(0, MAX_CONTENT_LENGTH - 3) + "...";
    }
    return content;
  }

  protected void sendMessageWithRetry(Object messageBody) throws IOException {
    IOException lastException = null;

    for (int i = 0; i < MAX_RETRY_TIMES; i++) {
      try {
        sendMessage(messageBody);
        return;
      } catch (IOException e) {
        lastException = e;
        log.warn("发送通知失败，正在重试 ({}/{})", i + 1, MAX_RETRY_TIMES);
        try {
          Thread.sleep(1000 * (i + 1));
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt();
          throw new IOException("发送消息被中断", ie);
        }
      }
    }

    throw new IOException("发送通知失败，已重试" + MAX_RETRY_TIMES + "次", lastException);
  }

  protected void sendMessage(Object messageBody) throws IOException {
    String json = objectMapper.writeValueAsString(messageBody);
    RequestBody body = RequestBody.create(json, JSON);
    Request request = new Request.Builder()
            .url(webhookUrl)
            .post(body)
            .header("Content-Type", "application/json")
            .build();

    try (Response response = client.newCall(request).execute()) {
      String responseBody = response.body() != null ? response.body().string() : null;

      if (!response.isSuccessful()) {
        throw new IOException("发送失败，HTTP状态码: " + response.code() + ", 响应: " + responseBody);
      }

      validateResponse(responseBody);
      log.info("通知发送成功");
    }
  }

  protected abstract void validateResponse(String responseBody) throws IOException;
} 