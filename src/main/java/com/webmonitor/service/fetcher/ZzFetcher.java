package com.webmonitor.service.fetcher;

import com.webmonitor.config.fetcher.ZzFetcherConfig;
import com.webmonitor.core.ContentFetcher;
import com.webmonitor.core.WebContent;
import com.webmonitor.util.HtmlUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ZzFetcher implements ContentFetcher {

  private List<WebContent> lastWeb = new ArrayList<>();
  private boolean isFirstLoad = true;
  private final ZzFetcherConfig zzFetcherConfig;

  public ZzFetcher(ZzFetcherConfig zzFetcherConfig) {
    this.zzFetcherConfig = zzFetcherConfig;
  }

  @Override
  public List<WebContent> fetch() throws Exception {
    if (isFirstLoad) {
      log.info("开始监控{}...", zzFetcherConfig.getName());
    } else {
      log.info("正在检查{}更新...", zzFetcherConfig.getName());
    }

    Document doc = HtmlUtil.getDocument(zzFetcherConfig.getUrl(), null, zzFetcherConfig.getCookie());

    Elements newsElements = doc.select(".list01 li");
    List<WebContent> currentWeb = new ArrayList<>();

    for (Element element : newsElements) {
      Element link = element.selectFirst("a");
      Element date = element.selectFirst("span");

      if (link != null) {
        String title = link.text();
        String url = link.attr("abs:href");
        String dateStr = date != null ? date.text() : "";

        WebContent webContent = WebContent.builder()
                .id(url)
                .title(title)
                .description(title)
                .link(url)
                .source(zzFetcherConfig.getName())
                .dateStr(dateStr)
                .category(zzFetcherConfig.getName())
                .build();

        currentWeb.add(webContent);
      }
    }

    List<WebContent> newWeb = new ArrayList<>();
    if (!isFirstLoad) {
      newWeb = findNewWebContent(currentWeb, lastWeb);
      log.info("{}检查完成，发现 {} 条新内容", zzFetcherConfig.getName(), newWeb.size());
    } else {
      log.info("首次加载{}，获取到 {} 条政策，不通知。内容如下：{}", zzFetcherConfig.getName(), currentWeb.size(), currentWeb);
    }

    lastWeb = currentWeb;
    isFirstLoad = false;
    return newWeb;
  }


}