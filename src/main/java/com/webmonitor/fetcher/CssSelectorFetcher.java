package com.webmonitor.fetcher;

import com.webmonitor.WebMonitorEnum;
import com.webmonitor.core.ContentFetcher;
import com.webmonitor.core.WebContent;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

@Slf4j
public class CssSelectorFetcher implements ContentFetcher {
  private List<WebContent> lastWeb = new ArrayList<>();
  private boolean isFirstLoad = true;

  @Override
  public List<WebContent> fetch() throws Exception {
    if (isFirstLoad) {
      log.info("开始监控{}...", getWebMonitorEnum().getName());
    } else {
      log.info("正在检查{}更新...", getWebMonitorEnum().getName());
    }

    List<WebContent> currentWeb = new ArrayList<>();

    try {
      Map<String, String> selectorDict = new HashMap<>();
      selectorDict.put("title", "#lg > map > area[shape='rect']");

      Map<String, String> headers = new HashMap<>();
      headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:128.0) Gecko/20100101 Firefox/128.0");
      headers.put("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7");

      Map<String, String> result = getByCssSelector(getWebMonitorEnum().getUrl(), selectorDict, headers);
      System.out.println(result);


      if (result.containsKey("title")) {
        String title = result.get("title");

        WebContent webContent = WebContent.builder()
                .id(title)
                .title(title)
                .description(title)
                .link(null)
                .source(getWebMonitorEnum().getName())
                .dateStr(null)
                .category(getWebMonitorEnum().getName())
                .build();

        currentWeb.add(webContent);
      }


    } catch (Exception e) {
      e.printStackTrace();
    }

    List<WebContent> newWeb = new ArrayList<>();
    if (!isFirstLoad) {
      newWeb = findNewWebContent(currentWeb);
      log.info("{}检查完成，发现 {} 条新内容", getWebMonitorEnum().getName(), newWeb.size());
    } else {
      log.info("首次加载{}，获取到 {} 条政策，不通知", getWebMonitorEnum().getName(), currentWeb.size());
    }

    lastWeb = currentWeb;
    isFirstLoad = false;
    return newWeb;
  }

  private List<WebContent> findNewWebContent(List<WebContent> currentWeb) {
    List<WebContent> newWeb = new ArrayList<>();
    for (WebContent news : currentWeb) {
      if (lastWeb.stream().noneMatch(n -> n.getId().equals(news.getId()))) {
        newWeb.add(news);
      }
    }
    return newWeb;
  }

  public Map<String, String> getByCssSelector(String url, Map<String, String> selectorDict, Map<String, String> headers) throws IOException {
    String html = getHtml(url, headers);
    Map<String, String> result = new LinkedHashMap<>();

    for (Map.Entry<String, String> entry : selectorDict.entrySet()) {
      String key = entry.getKey();
      String cssSelector = entry.getValue();

      String value = cssParse(html, cssSelector);
      result.put(key, value);
    }

    return result;
  }

  private String cssParse(String html, String cssSelector) {
    try {
      Document document = Jsoup.parse(html);
      Elements elements = document.select(cssSelector);

      if (!elements.isEmpty()) {
        Element first = elements.first();
        return first != null ? first.attr("title") : null;
      }

      throw new RuntimeException("无法获取文本信息");
    } catch (Exception e) {
      throw new RuntimeException("css 解析失败: " + e.getMessage());
    }
  }

  public String getHtml(String url, Map<String, String> headers) throws IOException {
    Document document;
    if (headers != null && !headers.isEmpty()) {
      document = Jsoup.connect(url)
              .headers(headers)
              .timeout(10000)
              .get();
    } else {
      document = Jsoup.connect(url)
              .timeout(10000)
              .get();
    }
    return document.html();
  }

  @Override
  public WebMonitorEnum getWebMonitorEnum() {
    return WebMonitorEnum.CssSelector;
  }
} 