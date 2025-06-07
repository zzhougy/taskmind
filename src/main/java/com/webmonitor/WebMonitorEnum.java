package com.webmonitor;

import com.webmonitor.core.ContentFetcher;
import com.webmonitor.fetcher.CssSelectorFetcher;
import com.webmonitor.fetcher.ZzFetcher;

public enum WebMonitorEnum {

  Zz("xxxx", "https://xxxxxx/", 60, new ZzFetcher()),
  CssSelector("ssss", "https://www.baidu.com", 6, new CssSelectorFetcher()),
  ;

  private final String name;
  private final String url;
  private final int intervalSeconds;
  private final ContentFetcher contentFetcher;

  WebMonitorEnum(String name, String url, int intervalSeconds, ContentFetcher contentFetcher) {
    this.name = name;
    this.url = url;
    this.intervalSeconds = intervalSeconds;
    this.contentFetcher = contentFetcher;
  }

  public String getName() {
    return name;
  }

  public String getUrl() {
    return url;
  }

  public ContentFetcher getContentFetcher() {
    return contentFetcher;
  }

  public int getIntervalSeconds() {
    return intervalSeconds;
  }
}
