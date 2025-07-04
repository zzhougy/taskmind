package com.webmonitor.service.observer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webmonitor.core.WebContent;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

@Slf4j
public class SlackWebObserver extends AbstractMessageObserver {
  private static final String WEBHOOK_BASE_URL_PROPERTY = "slack.webhook.base-url";

  public SlackWebObserver() {
    this(false);
  }

  public SlackWebObserver(boolean mentionAll) {
    super(WEBHOOK_BASE_URL_PROPERTY, null, mentionAll);
  }

  @Override
  public void send(List<WebContent> webContents) {
    if (webContents == null || webContents.isEmpty()) {
      log.warn("收到空的URL或内容更新");
      return;
    }

    try {
      String messageContent = formatMessage(webContents);
      SlackMessage message = SlackMessage.builder()
              .text(messageContent)
              .build();
      sendMessageWithRetry(message);
    } catch (Exception e) {
      log.error("发送Slack通知失败", e);
    }
  }

  private String formatMessage(List<WebContent> webContents) {
    StringBuilder sb = new StringBuilder();
    if (mentionAll) {
      sb.append("@channel\n");
    }
    sb.append("发现").append(webContents.size()).append("条新内容：\n\n");

    for (WebContent content : webContents) {
      sb.append("来源：").append(content.getSource()).append("\n")
              .append("类别：").append(content.getCategory()).append("\n")
              .append("时间：").append(content.getDateStr()).append("\n")
              .append("标题：").append(content.getTitle()).append("\n")
              .append("描述：").append(content.getDescription()).append("\n")
              .append("链接：").append(content.getLink()).append("\n")
              .append("------------------------\n");
    }

    return truncateContent(sb.toString());
  }

  @Override
  protected void validateResponse(String responseBody) throws IOException {
    if (!"ok".equalsIgnoreCase(responseBody)) {
      throw new IOException("Slack API返回错误：" + responseBody);
    }
  }

  @Data
  @Builder
  private static class SlackMessage {
    @JsonProperty("text")
    private String text;
  }
}