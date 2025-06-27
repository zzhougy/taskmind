package com.webmonitor.fetcher;

import com.webmonitor.config.fetcher.KeywordSelectorFetcherConfig;
import com.webmonitor.core.ContentFetcher;
import com.webmonitor.core.WebContent;
import com.webmonitor.util.CssSelectorUtil;
import com.webmonitor.util.HtmlUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class KeywordSelectorFetcher implements ContentFetcher {
  private List<WebContent> lastWeb = new ArrayList<>();
  private boolean isFirstLoad = true;
  private final KeywordSelectorFetcherConfig config;

  public KeywordSelectorFetcher(KeywordSelectorFetcherConfig config) {
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


    Document document = HtmlUtil.getDocument(config.getUrl(), null);
    Elements elements = document.getElementsContainingOwnText(config.getKeyword());
    String cssSelector = elements.first().cssSelector();
    Map<String, String> selectorDict = new HashMap<>();
    selectorDict.put("title", cssSelector + "|text");

    Map<String, String> result = CssSelectorUtil.getByCssSelector(config.getUrl(), selectorDict, null);

    if (result.containsKey("title")) {
      String title = result.get("title");
      log.info("{}获取到内容：{}", config.getName(), title);
      WebContent webContent = WebContent.builder()
              .id(title)
              .title(title)
              .description(title)
              .link(null)
              .source(config.getName())
              .dateStr(null)
              .category(config.getName())
              .build();

      currentWeb.add(webContent);
    }


    List<WebContent> newWeb = new ArrayList<>();
    if (!isFirstLoad) {
      newWeb = findNewWebContent(currentWeb, lastWeb);
      log.info("{}检查完成，发现 {} 条新内容", config.getName(), newWeb.size());
    } else {
      log.info("首次加载{}，获取到 {} 条政策，不通知", config.getName(), currentWeb.size());
    }

    lastWeb = currentWeb;
    isFirstLoad = false;
    return newWeb;
  }

}