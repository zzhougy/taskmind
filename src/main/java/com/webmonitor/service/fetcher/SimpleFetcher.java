package com.webmonitor.service.fetcher;

import com.webmonitor.config.fetcher.SimpleFetcherConfig;
import com.webmonitor.core.ContentFetcher;
import com.webmonitor.core.WebContent;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SimpleFetcher implements ContentFetcher {

  private List<WebContent> lastWeb = new ArrayList<>();
  private boolean isFirstLoad = true;
  private final SimpleFetcherConfig config;

  public SimpleFetcher(SimpleFetcherConfig config) {
    this.config = config;
  }

  @Override
  public List<WebContent> fetch() throws Exception {
    if (isFirstLoad) {
      log.info("开始监控{}...", config.getName());
    } else {
      log.info("正在检查{}更新...", config.getName());
    }

    List<WebContent> currentWeb = new ArrayList<>();
    WebContent webContent = WebContent.builder()
            .id("url")
            .title(config.getContent())
            .description("title")
            .link("url")
            .source("")
            .dateStr("dateStr")
            .category("")
            .build();

    currentWeb.add(webContent);

    List<WebContent> newWeb = new ArrayList<>();
    if (!isFirstLoad) {
      newWeb = findNewWebContent(currentWeb, lastWeb);
      log.info("{}检查完成，发现 {} 条新内容", config.getName(), newWeb.size());
    } else {
      log.info("首次加载{}，获取到 {} 条政策，不通知。内容如下：{}", config.getName(), currentWeb.size(), currentWeb);
    }

    lastWeb = currentWeb;
    isFirstLoad = false;
    return newWeb;
  }


}