package com.webmonitor.service.observer;

import cn.hutool.core.collection.CollectionUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webmonitor.core.WebContent;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
public class QyWeixinWebObserver extends AbstractMessageObserver {
  private static final String WEBHOOK_BASE_URL_PROPERTY = "weixin.webhook.base-url";
  private static final String WEBHOOK_KEY_PROPERTY = "weixin.webhook.key";

  public QyWeixinWebObserver() {
    this(false);
  }

  public QyWeixinWebObserver(boolean mentionAll) {
    super(WEBHOOK_BASE_URL_PROPERTY, WEBHOOK_KEY_PROPERTY, mentionAll);
  }

  @Override
  public void send(List<WebContent> webContents) {
    if (CollectionUtil.isEmpty(webContents)) {
      log.warn("收到空的URL或内容更新");
      return;
    }

    try {
      String messageContent = formatMessage(webContents);
      WeixinMessage message = WeixinMessage.builder()
              .text(TextContent.builder().content(messageContent).build())
              .build();
      sendMessageWithRetry(message);
    } catch (Exception e) {
      log.error("发送企业微信通知失败", e);
    }
  }

  private String formatMessage(List<WebContent> webContents) {
    StringBuilder messageBuilder = new StringBuilder();
    messageBuilder.append("🔔 网站内容更新通知\n\n");
    messageBuilder.append("📅 时间：").append(LocalDateTime.now().format(DATE_FORMATTER)).append("\n");
    messageBuilder.append("📝 更新内容：\n").append(truncateContent(""));

    if (mentionAll) {
      messageBuilder.append("\n\n@所有人");
    }

    return messageBuilder.toString();
  }

  @Override
  protected void validateResponse(String responseBody) throws IOException {
    WeixinResponse response = new ObjectMapper().readValue(responseBody, WeixinResponse.class);
    if (response.getErrcode() != 0) {
      throw new IOException("发送失败，错误码: " + response.getErrcode() +
              ", 错误信息: " + response.getErrmsg());
    }
  }

  @Data
  @Builder
  private static class WeixinMessage {
    private final String msgtype = "text";
    private TextContent text;
  }

  @Data
  @Builder
  private static class TextContent {
    private String content;
  }

  @Data
  private static class WeixinResponse {
    private int errcode;
    private String errmsg;
  }
} 