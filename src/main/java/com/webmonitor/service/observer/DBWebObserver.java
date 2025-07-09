package com.webmonitor.service.observer;

import com.webmonitor.core.WebContent;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class DBWebObserver implements WebObserver {



  @Override
  public void send(List<WebContent> webContents) {
    if (webContents == null || webContents.isEmpty()) {
      log.warn("收到空的内容");
      return;
    }

    log.info("开始保存内容到数据库");
    webContents.forEach(webContent -> {
      log.info("保存内容：{}", webContent);
    });

  }


}