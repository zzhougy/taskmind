package com.webmonitor.core;

import com.webmonitor.WebMonitorEnum;

import java.util.ArrayList;
import java.util.List;

public interface ContentFetcher {
  List<WebContent> fetch() throws Exception;
  WebMonitorEnum getWebMonitorEnum();


  default List<WebContent> findNewWebContent(List<WebContent> currentWeb, List<WebContent> lastWeb) {
    List<WebContent> newWeb = new ArrayList<>();
    for (WebContent news : currentWeb) {
      if (lastWeb.stream().noneMatch(n -> n.getId().equals(news.getId()))) {
        newWeb.add(news);
      }
    }
    return newWeb;
  }
} 