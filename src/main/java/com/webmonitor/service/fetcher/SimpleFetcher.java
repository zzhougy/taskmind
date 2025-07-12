package com.webmonitor.service.fetcher;

import com.webmonitor.config.fetcher.SimpleFetcherConfig;
import com.webmonitor.core.ContentFetcher;
import com.webmonitor.core.WebContent;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SimpleFetcher implements ContentFetcher {

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
            .id(null)
            .title(config.getContent())
            .description(null)
            .url(null)
            .source(null)
            .dateStr(null)
            .category(null)
            .build();

    currentWeb.add(webContent);


    isFirstLoad = false;
    return currentWeb;
  }


}