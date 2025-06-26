package com.webmonitor.observer;

import com.webmonitor.core.WebContent;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ConsoleWebObserver implements WebObserver {

  @Override
  public void send(List<WebContent> news) {
    log.info("发现 {} 条新内容:", news.size());
    for (WebContent item : news) {
      log.info("来源: {}", item.getSource());
      log.info("类别: {}", item.getCategory());
      log.info("时间: {}", item.getDateStr());
      log.info("标题: {}", item.getTitle());
      log.info("描述: {}", item.getDescription());
      log.info("链接: {}", item.getLink());
      log.info("------------------------");
    }
  }
} 