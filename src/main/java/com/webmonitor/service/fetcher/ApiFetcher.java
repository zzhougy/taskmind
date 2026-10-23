package com.webmonitor.service.fetcher;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.webmonitor.config.fetcher.ApiFetcherConfig;
import com.webmonitor.core.ContentFetcher;
import com.webmonitor.core.WebContent;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 最简单的API监控demo：定时GET一个API，比较返回值是否发生变化，变化则通知。
 * 例如监控百度热搜API：https://top.baidu.com/api/board?platform=wise&tab=realtime
 */
@Slf4j
public class ApiFetcher implements ContentFetcher {

  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
  private static final int EXCERPT_LENGTH = 500;

  private String lastBodyHash;
  private boolean isFirstLoad = true;
  private final ApiFetcherConfig config;

  public ApiFetcher(ApiFetcherConfig config) {
    this.config = config;
  }

  @Override
  public List<WebContent> fetch() throws Exception {
    if (isFirstLoad) {
      log.info("开始监控{}...", config.getName());
    } else {
      log.info("正在检查{}更新...", config.getName());
    }

    List<WebContent> newWeb = new ArrayList<>();
    String body;
    try (HttpResponse response = HttpRequest.get(config.getUrl())
            .timeout(10000)
            .execute()) {
      body = response.body();
    }
    String hash = DigestUtil.md5Hex(body);

    if (isFirstLoad) {
      log.info("首次加载{}，记录基线返回值，不通知。", config.getName());
    } else if (!hash.equals(lastBodyHash)) {
      log.info("{}检测到API返回值变化", config.getName());
      WebContent webContent = WebContent.builder()
              .id("api-change-" + System.currentTimeMillis())
              .title(config.getName() + " API返回值发生变化")
              .description(toExcerpt(body))
              .url(config.getUrl())
              .source(config.getName())
              .dateStr(LocalDateTime.now().format(DATE_TIME_FORMATTER))
              .category("API变化")
              .build();
      newWeb.add(webContent);
    } else {
      log.info("{}无变化", config.getName());
    }

    lastBodyHash = hash;
    isFirstLoad = false;
    return newWeb;
  }

  private String toExcerpt(String body) {
    if (body == null) {
      return "";
    }
    String oneLine = body.replaceAll("\\s+", " ");
    return oneLine.length() > EXCERPT_LENGTH ? oneLine.substring(0, EXCERPT_LENGTH) + "..." : oneLine;
  }
}
