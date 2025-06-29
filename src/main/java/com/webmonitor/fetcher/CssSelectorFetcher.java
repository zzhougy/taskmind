package com.webmonitor.fetcher;

import com.webmonitor.config.fetcher.CssSelectorFetcherConfig;
import com.webmonitor.core.ContentFetcher;
import com.webmonitor.core.WebContent;
import com.webmonitor.util.JsoupUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class CssSelectorFetcher implements ContentFetcher {
  private List<WebContent> lastWeb = new ArrayList<>();
  private boolean isFirstLoad = true;
  private final CssSelectorFetcherConfig cssSelectorFetcherConfig;

  public CssSelectorFetcher(CssSelectorFetcherConfig cssSelectorFetcherConfig) {
    this.cssSelectorFetcherConfig = cssSelectorFetcherConfig;
  }

  @Override
  public List<WebContent> fetch() throws Exception {
    if (isFirstLoad) {
      log.info("开始监控{}...", cssSelectorFetcherConfig.getName());
    } else {
      log.info("正在检查{}更新...", cssSelectorFetcherConfig.getName());
    }

    List<WebContent> currentWeb = new ArrayList<>();

    try {
      Map<String, String> selectorDict = new HashMap<>();
//      selectorDict.put("title", "#lg > map > area[shape='rect']");
//      selectorDict.put("title", "*[id=\"lg\"] > map > area[shape='rect']");
      selectorDict.put("title", cssSelectorFetcherConfig.getCssSelector());

      Map<String, String> result = JsoupUtil.getByCssSelector(cssSelectorFetcherConfig.getUrl(), selectorDict,
              null, cssSelectorFetcherConfig.getCookie());
      System.out.println(result);


      if (result.containsKey("title")) {
        String title = result.get("title");

        WebContent webContent = WebContent.builder()
                .id(title)
                .title(title)
                .description(title)
                .link(null)
                .source(cssSelectorFetcherConfig.getName())
                .dateStr(null)
                .category(cssSelectorFetcherConfig.getName())
                .build();

        currentWeb.add(webContent);
      }


    } catch (Exception e) {
      throw e;
    }

    List<WebContent> newWeb = new ArrayList<>();
    if (!isFirstLoad) {
      newWeb = findNewWebContent(currentWeb, lastWeb);
      log.info("{}检查完成，发现 {} 条新内容", cssSelectorFetcherConfig.getName(), newWeb.size());
    } else {
      log.info("首次加载{}，获取到 {} 条政策，不通知", cssSelectorFetcherConfig.getName(), currentWeb.size());
    }

    lastWeb = currentWeb;
    isFirstLoad = false;
    return newWeb;
  }

}