package com.webmonitor.fetcher;

import com.webmonitor.WebMonitorEnum;
import com.webmonitor.core.ContentFetcher;
import com.webmonitor.core.WebContent;
import com.webmonitor.util.XPathUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class XPathFetcher implements ContentFetcher {
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
//      selectorDict.put("title", "#lg > map > area[shape='rect']");
      selectorDict.put("title", "*[id=\"lg\"] > map > area[shape='rect']");

      Map<String, String> headers = new HashMap<>();
      headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:128.0) Gecko/20100101 Firefox/128.0");
      headers.put("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7");

      Map<String, String> result = XPathUtil.getXpathSelector(getWebMonitorEnum().getUrl(), selectorDict, headers);
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
      newWeb = findNewWebContent(currentWeb, lastWeb);
      log.info("{}检查完成，发现 {} 条新内容", getWebMonitorEnum().getName(), newWeb.size());
    } else {
      log.info("首次加载{}，获取到 {} 条政策，不通知", getWebMonitorEnum().getName(), currentWeb.size());
    }

    lastWeb = currentWeb;
    isFirstLoad = false;
    return newWeb;
  }



  @Override
  public WebMonitorEnum getWebMonitorEnum() {
    return WebMonitorEnum.XPath;
  }
}
